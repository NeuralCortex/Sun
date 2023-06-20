package com.fx.sun.tools;

import com.fx.sun.pojo.EleTimePOJO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import org.shredzone.commons.suncalc.SunPosition;

/**
 *
 * @author pscha
 */
public class StundenSchleifenThread extends Thread {

    private final LocalDate now;
    private final double lat;
    private final double lon;
    private int hour = -9999;
    private final List<EleTimePOJO> listSchleifen = new ArrayList<>();
    private HashMap<Integer, List<EleTimePOJO>> map;

    public StundenSchleifenThread(LocalDate now, double lat, double lon, int hour, HashMap<Integer, List<EleTimePOJO>> map) {
        this.now = now;
        this.lat = lat;
        this.lon = lon;
        this.hour = hour;
        this.map = map;
    }

    @Override
    public void run() {
        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfYear());

        int count = 0;
        for (LocalDate date = firstDay; date.isBefore(lastDay); date = date.plusDays(1)) {

            LocalDateTime localDateTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, 0);
            
            SunPosition sunPosition = SunPosition.compute().on(localDateTime).at(lat, lon).timezone(TimeZone.getTimeZone("Egypt")).execute();

            double azi = sunPosition.getAzimuth();
            double alti = sunPosition.getAltitude();
            //System.out.println("azi: "+azi+" alti: "+alti);
            listSchleifen.add(new EleTimePOJO(count++, azi, alti, localDateTime));
        }
        listSchleifen.sort(Comparator.comparing(t -> t.getTime()));
        map.put(hour, listSchleifen);
    }
}
