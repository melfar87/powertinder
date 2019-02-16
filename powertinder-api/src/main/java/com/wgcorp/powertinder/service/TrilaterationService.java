package com.wgcorp.powertinder.service;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.wgcorp.powertinder.domain.entity.Position;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrilaterationService {
    private static final double EARTH_RADIUS = 6371; // km
    public static final double EQUATORIAL_RADIUS = 6378137.0;

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    public Position approximateLocation(List<Position> positions, double... distances) {
        double[][] positionsArray = new double[positions.size()][3];

        for (int i = 0; i < positions.size(); i++) {
            Position currentPos = positions.get(i);

            double[] posInRad = {Math.toRadians(currentPos.getLat()), Math.toRadians(currentPos.getLon()), 0};
            positionsArray[i] = this.lla2ecef(posInRad);
        }

        double[][] posArrayInvert = new double[positions.size()][3];
        for (int i = 0; i < positionsArray.length; i++) {
            posArrayInvert[i][0] = positionsArray[i][1];
            posArrayInvert[i][1] = positionsArray[i][0];
            posArrayInvert[i][2] = positionsArray[i][2];
        }


        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(posArrayInvert, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        double[] centroid = optimum.getPoint().toArray();
        double[] centroidWithAlt = {centroid[1], centroid[0], 4784.083079950711};
        double[] centroidLatLon = this.ecef2lla(centroidWithAlt);

        Position result = new Position();
        result.setLat(Math.toDegrees(centroidLatLon[0]));
        result.setLon(Math.toDegrees(centroidLatLon[1]));

        return result;
    }

    /**
     * Returns the destination point from this point having travelled the given distance on the
     * given initial bearing (bearing normally varies around path followed).
     *
     * @param start    the start point
     * @param distance the distance travelled, in same units as earth radius (default: meters)
     * @param bearing  the initial bearing in degrees from north
     * @return the destination point
     * @see <a href="http://www.movable-type.co.uk/scripts/latlon.js">latlon.js</a>
     */
    public Position destinationPoint(Position start, double distance, float bearing) {
        double theta = Math.toRadians(bearing);
        double delta = distance / EQUATORIAL_RADIUS; // angular distance in radians

        double phi1 = Math.toRadians(start.getLat());
        double lambda1 = Math.toRadians(start.getLon());

        double phi2 = Math.asin(Math.sin(phi1) * Math.cos(delta)
                + Math.cos(phi1) * Math.sin(delta) * Math.cos(theta));
        double lambda2 = lambda1 + Math.atan2(Math.sin(theta) * Math.sin(delta) * Math.cos(phi1),
                Math.cos(delta) - Math.sin(phi1) * Math.sin(phi2));

        Position result = new Position();
        result.setLat(Math.toDegrees(phi2));
        result.setLon(Math.toDegrees(lambda2));

        return result;
    }

//    public double[] triangulation(double lat0, double lon0, double r0, double lat1, double lon1, double r1, double lat2, double lon2, double r2) {
//        // Convert to cartesian
//        double[] p0 = latlon2cartesian(lat0, lon0);
//        double[] p1 = latlon2cartesian(lat1, lon1);
//        double[] p2 = latlon2cartesian(lat2, lon2);
//
//        // Convert so that p0 sits at (0,0)
//        double[] p0a = new double[]{0, 0, 0};
//        double[] p1a = new double[]{p1[X] - p0[X], p1[Y] - p0[Y], p1[Z] - p0[Z]};
//        double[] p2a = new double[]{p2[X] - p0[X], p2[Y] - p0[Y], p2[Z] - p0[Z]};
//
//        // All distances refers to p0, the origin
//        Double p1distance = distance(p0a, p1a);
//        if (p1distance == null)
//            return null;
//        Double p2distance = distance(p0a, p2a);
//        if (p2distance == null)
//            return null;
//
//        // unit vector of p1a
//        double[] p1a_ev = new double[]{p1a[X] / p1distance, p1a[Y] / p1distance, p1a[X] / p1distance};
//        // dot product of p1a_ev with p2a
//        double p2b_x = p1a_ev[X] * p2a[X] + p1a_ev[Y] * p2a[Y] + p1a_ev[Z] * p2a[Z];
//        // finding the y of p2b (for same distance of p2a from p0a)
//        double p2b_y = Math.sqrt(Math.abs(Math.pow(p2distance, 2) - Math.pow(p2b_x, 2)));
//
//        // Convert so that p1 stays on the x line (rotates the plane)
//        double[] p0b = new double[]{0, 0, 0};
//        double[] p1b = new double[]{p1distance, 0, 0};
//        double[] p2b = new double[]{p2b_x, p2b_y, 0};
//
//        double d = p1distance, i = p2b_x, j = p2b_y;
//
//        double x = (Math.pow(r0, 2) - Math.pow(r1, 2) + Math.pow(d, 2)) / (2 * d);
//        double y = (Math.pow(r0, 2) - Math.pow(r2, 2) + Math.pow(i, 2) + Math.pow(j, 2)) / (2 * j) - (i / j) * x;
//
//        double[] pb = new double[]{x, y, 0};
//        Double pbdistance = distance(p0b, pb);
//        if (pbdistance == null)
//            return null;
//
//        // Opposite operation done for converting points from coordinate system a to b
//        double pax = pb[X] / p1a_ev[X] + pb[Y] / p1a_ev[Y] + pb[Z] / p1a_ev[Z];
//        double[] pa = new double[]
//                {
//                        pax,
//                        Math.sqrt(Math.abs(Math.pow(pbdistance, 2) - Math.pow(pax, 2))),
//                        0
//                };
//
//        // Opposite operation done for converting points from coordinate system to a
//        double p[] = new double[]
//                {
//                        pa[X] + p0[X],
//                        pa[Y] + p0[Y],
//                        pa[Z] + p0[Z]
//                };
//
//        // Reconvert to lat/lon
//        return cartesian2latlon(p[X], p[Y], p[Z]);
//    }
//
//    /**
//     * Computes distance between points in any dimension.
//     *
//     * @param p0 point 0
//     * @param p1 point 1
//     * @return distance between points
//     */
//    private Double distance(double[] p0, double[] p1) {
//        // Must be of same dimension
//        if (p0.length != p1.length)
//            return null;
//
//        // Calculate distance
//        double val = 0;
//        for (int n = 0; n < p0.length; n++)
//            val += Math.pow(p1[n] - p0[n], 2);
//        return Math.sqrt(val);
//    }
//
//    /**
//     * Return only the best of <num> points.
//     *
//     * @param num       the max of the best of points
//     * @param wifiSpots all the points to analyze
//     * @return the best of <num> points
//     */
//    public List<Object> bestOf(int num, Object... wifiSpots) {
//        return null;
//    }
//
//    /**
//     * Converts to Cartesian points
//     *
//     * @param lat latitude
//     * @param lon longitude
//     * @return point in x,y,z
//     */
//    public double[] latlon2cartesian(double lat, double lon) {
//        return new double[]
//                {
//                        Math.cos(lon) * Math.cos(lat) * EARTH_RADIUS,
//                        Math.sin(lon) * Math.cos(lat) * EARTH_RADIUS,
//                        Math.sin(lat) * EARTH_RADIUS
//                };
//    }
//
//    /**
//     * Reconvert back to lat/lon.
//     *
//     * @param x x value
//     * @param y y value
//     * @param z z value
//     * @return point in lat,lon
//     */
//    public double[] cartesian2latlon(double x, double y, double z) {
//        return new double[]
//                {
//                        Math.atan(y / x),
//                        Math.acos(z / EARTH_RADIUS)
//                };
//    }
//
//    public List<Double> convertGpsToECEF(double lat, double longi, float alt) {
//
//        double a=6378.1;
//        double b=6356.8;
//        double N;
//        double e= 1-(Math.pow(b, 2)/Math.pow(a, 2));
//        N= a/(Math.sqrt(1.0-(e*Math.pow(Math.sin(Math.toRadians(lat)), 2))));
//        double cosLatRad=Math.cos(Math.toRadians(lat));
//        double cosLongiRad=Math.cos(Math.toRadians(longi));
//        double sinLatRad=Math.sin(Math.toRadians(lat));
//        double sinLongiRad=Math.sin(Math.toRadians(longi));
//        double x =(N+0.001*alt)*cosLatRad*cosLongiRad;
//        double y =(N+0.001*alt)*cosLatRad*sinLongiRad;
//        double z =((Math.pow(b, 2)/Math.pow(a, 2))*N+0.001*alt)*sinLatRad;
//
//        List<Double> ecef= new ArrayList<>();
//        ecef.add(x);
//        ecef.add(y);
//        ecef.add(z);
//
//        return ecef;
//    }


    // WGS84 ellipsoid constants
//    private final double a = 6378137; // radius
    private final double a = 6378.137; // radius
    private final double e = 8.1819190842622e-2;  // eccentricity

    private final double asq = Math.pow(a, 2);
    private final double esq = Math.pow(e, 2);

    /*
     *
     *  ECEF - Earth Centered Earth Fixed
     *
     *  LLA - Lat Lon Alt
     *
     *  ported from matlab code at
     *  https://gist.github.com/1536054
     *     and
     *  https://gist.github.com/1536056
     */


    public double[] ecef2lla(double[] ecef) {
        double x = ecef[0];
        double y = ecef[1];
        double z = ecef[2];

        double b = Math.sqrt(asq * (1 - esq));
        double bsq = Math.pow(b, 2);
        double ep = Math.sqrt((asq - bsq) / bsq);
        double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double th = Math.atan2(a * z, b * p);

        double lon = Math.atan2(y, x);
        double lat = Math.atan2((z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq * a * Math.pow(Math.cos(th), 3)));
        double N = a / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
        double alt = p / Math.cos(lat) - N;

        // mod lat to 0-2pi
        lon = lon % (2 * Math.PI);

        // correction for altitude near poles left out.

        double[] ret = {lat, lon, alt};

        return ret;
    }

    public double[] lla2ecef(double[] lla) {
        double lat = lla[0];
        double lon = lla[1];
        double alt = lla[2];

        double N = a / Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2));

        double x = (N + alt) * Math.cos(lat) * Math.cos(lon);
        double y = (N + alt) * Math.cos(lat) * Math.sin(lon);
        double z = ((1 - esq) * N + alt) * Math.sin(lat);

        double[] ret = {x, y, z};
        return ret;
    }

    public Position searchLocation(Position startPosition, double distanceKm) {
        return null;
    }
}
