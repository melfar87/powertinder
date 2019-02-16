package com.wgcorp.powertinder.service;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.wgcorp.powertinder.domain.entity.Position;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrilaterationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrilaterationService.class);

    // WGS84 ellipsoid constants
    private static final double EQUATORIAL_RADIUS = 6378137.0;  // m

    private final double a = 6378.137; // radius in km
    private final double e = 8.1819190842622e-2;  // eccentricity
    private final double asq = Math.pow(a, 2);
    private final double esq = Math.pow(e, 2);

    /**
     * @param positions
     * @param distances
     * @return
     */
    public Position approximateLocation(List<Position> positions, double... distances) {
        double[][] positionsInRadian = new double[positions.size()][3];

        // Convert lat/long positions in degrees to radians
        for (int i = 0; i < positions.size(); i++) {
            Position currentPos = positions.get(i);

            double[] posInRad = {Math.toRadians(currentPos.getLat()), Math.toRadians(currentPos.getLon()), 0};
            positionsInRadian[i] = this.lla2ecef(posInRad);
        }

        // Has to invert X and Y in the positions array for call to next operation
        double[][] positionInRadianInverted = new double[positions.size()][3];
        for (int i = 0; i < positionsInRadian.length; i++) {
            positionInRadianInverted[i][0] = positionsInRadian[i][1];
            positionInRadianInverted[i][1] = positionsInRadian[i][0];
            positionInRadianInverted[i][2] = positionsInRadian[i][2];
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positionInRadianInverted, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        double[] centroid = optimum.getPoint().toArray();
        double[] centroidWithAlt = {centroid[1], centroid[0], 4784.083079950711};

        // Convert back to lat/lon
        double[] centroidLatLon = this.ecef2lla(centroidWithAlt);

        Position result = new Position();
        result.setLat(Math.toDegrees(centroidLatLon[0]));
        result.setLon(Math.toDegrees(centroidLatLon[1]));
        result.setAt(0.0);

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
        result.setAt(0.0);

        return result;
    }

    /**
     * Convert ECEF to Lat/Long
     * @param ecef
     * @return
     */
    private double[] ecef2lla(double[] ecef) {
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

    /**
     * Convert Lat/Long to ECEF
     *
     * @param lla
     * @return
     */
    private double[] lla2ecef(double[] lla) {
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

}
