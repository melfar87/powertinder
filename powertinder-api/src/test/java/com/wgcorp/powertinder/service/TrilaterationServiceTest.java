package com.wgcorp.powertinder.service;


import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

public class TrilaterationServiceTest {

    private TrilaterationService trilaterationService = new TrilaterationService();

    // Rond Point PLAISIR : 48.831647, 1.941546
    // Point 1 - Chambourcy Rond Point Tesla : 48.911322, 2.032490, 11.08
    // Point 2 - Versailles Place d'arme : 48.803304, 2.126466, 13.9
    // Point 3 - Beynes station Total : 48.850224, 1.877063, 5.15
    // Point 4 - Plaisir N12 : 48.800583, 1.962897, 3.81

    // https://github.com/gastoneb/Vector3D_LLA_conversion_example/blob/master/src/main/scala/Conversion.scala

    @Test
    public void shouldConvertToCartesianCoordinates() {
        // given

        // when
//        List<Double> p1CartesianCoord = trilaterationService.convertGpsToECEF(48.911322, 2.032490, 0);
        double[] p1Lla = {Math.toRadians(48.911322), Math.toRadians(2.032490), 0};
        double[] p1Ecef = trilaterationService.lla2ecef(p1Lla);

//        List<Double> p2CartesianCoord = trilaterationService.convertGpsToECEF(48.803304, 2.126466, 0);
        double[] p2Lla = {Math.toRadians(48.803304), Math.toRadians(2.126466), 0};
        double[] p2Ecef = trilaterationService.lla2ecef(p2Lla);

//        List<Double> p3CartesianCoord = trilaterationService.convertGpsToECEF(48.850224, 1.877063, 0);
        double[] p3Lla = {Math.toRadians(48.850224), Math.toRadians(1.877063), 0};
        double[] p3Ecef = trilaterationService.lla2ecef(p3Lla);

//        List<Double> p4CartesianCoord = trilaterationService.convertGpsToECEF( 48.800583, 1.962897, 0);
        double[] p4Lla = {Math.toRadians(48.800583), Math.toRadians(1.962897), 0};
        double[] p4Ecef = trilaterationService.lla2ecef(p4Lla);

        System.out.println("P1");
//        System.out.println(p1CartesianCoord.get(0) + "," + p1CartesianCoord.get(1) + "," + p1CartesianCoord.get(2));
        System.out.println(p1Ecef[0] + "," + p1Ecef[1] + "," + p1Ecef[2]);

        System.out.println("P2");
//        System.out.println(p2CartesianCoord.get(0) + "," + p2CartesianCoord.get(1) + "," + p2CartesianCoord.get(2));
        System.out.println(p2Ecef[0] + "," + p2Ecef[1] + "," + p2Ecef[2]);

        System.out.println("P3");
//        System.out.println(p3CartesianCoord.get(0) + "," + p3CartesianCoord.get(1) + "," + p3CartesianCoord.get(2));
        System.out.println(p3Ecef[0] + "," + p3Ecef[1] + "," + p3Ecef[2]);

        System.out.println("P4");
//        System.out.println(p4CartesianCoord.get(0) + "," + p4CartesianCoord.get(1) + "," + p4CartesianCoord.get(2));
        System.out.println(p4Ecef[0] + "," + p4Ecef[1] + "," + p4Ecef[2]);

//        double[][] positions = {{p1CartesianCoord.get(1), p1CartesianCoord.get(0)}, {p2CartesianCoord.get(1), p2CartesianCoord.get(0)}, {p3CartesianCoord.get(1), p3CartesianCoord.get(0)}};
        double[][] positions = {{p1Ecef[1], p1Ecef[0]}, {p2Ecef[1], p2Ecef[0]}, {p3Ecef[1], p3Ecef[0]}, {p4Ecef[1], p4Ecef[0]}};
        double[] distances = {11.08, 13.9, 5.15, 3.81};

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

// the answer
        double[] centroid = optimum.getPoint().toArray();
        double[] centroidWithAlt = {centroid[1], centroid[0], 4784.083079950711};
        double[] centroidLatLon = trilaterationService.ecef2lla(centroidWithAlt);

        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);

        System.out.println("CENTROID");
        System.out.println(centroid[1] + "," + centroid[0]);
        System.out.println(Math.toDegrees(centroidLatLon[0]) + "," + Math.toDegrees(centroidLatLon[1]));

        //        double[] latLngCoord = trilaterationService.cartesian2latlon(cartesianCoord[0], cartesianCoord[1], cartesianCoord[2]);
//        double[] latLngCoord = trilaterationService.cartesian2latlon(4197231.25806638, 148953.566467734, 4784083.98431464);
//        trilaterationService.

        // then
//        assertThat(latLngCoord).containsExactly(48.911322, 2.032490);
//        assertThat(cartesianCoord).containsExactly(4197231.25806638, 148953.566467734);
    }

//    @Test
//    public void testTriangulation() {
//       double loc[] = trilaterationService.triangulation(48.911322, 2.032490, 11.08, 48.803304, 2.126466, 13.9, 48.850224, 1.877063, 5.15);
//
//       System.out.println(loc[0] + "," + loc[1]);
//    }
//
//    @Test
//    public void testConvert() {
//       List<Double> loc = trilaterationService.convertGpsToECEF(48.831647, 1.941546, 0);
//
//       System.out.println(loc.get(0) + "," + loc.get(1) + "," + loc.get(2));
//    }

}