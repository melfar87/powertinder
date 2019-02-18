package com.wgcorp.powertinder.service;


import com.wgcorp.powertinder.domain.entity.Position;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrilaterationServiceTest {

    private TrilaterationService trilaterationService = new TrilaterationService();

    @Test
    public void shouldApproximateLocation() {
        // given
        Position p1 = new Position(48.911322, 2.032490);
        Position p2 = new Position(48.803304, 2.126466);
        Position p3 = new Position(48.850224, 1.877063);
        Position p4 = new Position(48.800583, 1.962897);
        double[] distances = {11.08, 13.9, 5.15, 3.81};

        // when
        Position pos = trilaterationService.approximateLocation(List.of(p1, p2, p3, p4), distances);

        // then
        assertThat(pos.getLat()).isEqualTo(48.86624358044333);
        assertThat(pos.getLon()).isEqualTo(1.941750589026748);

    }

}