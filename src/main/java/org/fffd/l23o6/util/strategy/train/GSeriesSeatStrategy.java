package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import org.fffd.l23o6.pojo.entity.TrainEntity;


public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static final GSeriesSeatStrategy INSTANCE = new GSeriesSeatStrategy();
     
    public final Map<Integer, String> BUSINESS_SEAT_MAP = new HashMap<>();
    public final Map<Integer, String> FIRST_CLASS_SEAT_MAP = new HashMap<>();
    public final Map<Integer, String> SECOND_CLASS_SEAT_MAP = new HashMap<>();
    public final Map<Integer, String> NO_SEAT_MAP = new HashMap<>();

    private final Map<GSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, BUSINESS_SEAT_MAP);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, FIRST_CLASS_SEAT_MAP);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, SECOND_CLASS_SEAT_MAP);
        put(GSeriesSeatType.NO_SEAT, NO_SEAT_MAP);
    }};
    
    private final Map<GSeriesSeatType, Integer> PRICE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, 100);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, 50);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, 20);
        put(GSeriesSeatType.NO_SEAT, 10);
    }};
    
    public Map<GSeriesSeatType, Map<Integer, String>> getTYPE_MAP() {return TYPE_MAP;}
    
    private GSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("1车1A","1车1C","1车1F")) {
            BUSINESS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("2车1A","2车1C","2车1D","2车1F","2车2A","2车2C","2车2D","2车2F","3车1A","3车1C","3车1D","3车1F")) {
            FIRST_CLASS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("4车1A","4车1B","4车1C","4车1D","4车2F","4车2A","4车2B","4车2C","4车2D","4车2F","4车3A","4车3B","4车3C","4车3D","4车3F")) {
            SECOND_CLASS_SEAT_MAP.put(counter++, s);
        }
        
        for (String s: Arrays.asList("无座1", "无座2", "无座3", "无座4", "无座5", "无座6", "无座7", "无座8", "无座9", "无座10", "无座11", "无座12", "无座13", "无座14", "无座15")) {
            NO_SEAT_MAP.put(counter++, s);
        }
        
    }

    public enum GSeriesSeatType implements SeatType {
        BUSINESS_SEAT("商务座"), FIRST_CLASS_SEAT("一等座"), SECOND_CLASS_SEAT("二等座"), NO_SEAT("无座");
        private String text;
        GSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, GSeriesSeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        int startIndex = switch (type) {
            case FIRST_CLASS_SEAT -> BUSINESS_SEAT_MAP.size();
            case SECOND_CLASS_SEAT -> BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size();
            case NO_SEAT -> BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size();
            default -> 0;
        };
        for (int i = startIndex; i < startIndex + TYPE_MAP.get(type).size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i]) break;
                if (j == endStationIndex - 1) {
                    for (int k = startStationIndex; k < endStationIndex; k++) {
                        seatMap[k][i] = true;
                    }
                    return TYPE_MAP.get(type).get(i);
                }
            }
        }

        return null;
    }

    public Map<GSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        int businessSeatCount = 0;
        int firstClassSeatCount = 0;
        int secondClassSeatCount = 0;
        int noSeatCount = 0;

        for (int i = 0; i < BUSINESS_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    businessSeatCount++;
            }
        }
        
        for (int i = BUSINESS_SEAT_MAP.size(); i < BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    firstClassSeatCount++;
            }
        }
        
        for (int i = BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size(); i < BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    secondClassSeatCount++;
            }
        }
        
        for (int i = BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size(); i < BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size() + NO_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    noSeatCount++;
            }
        }
        
        return Map.of(GSeriesSeatType.BUSINESS_SEAT, businessSeatCount, GSeriesSeatType.FIRST_CLASS_SEAT, firstClassSeatCount, GSeriesSeatType.SECOND_CLASS_SEAT, secondClassSeatCount, GSeriesSeatType.NO_SEAT, noSeatCount);
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size() + NO_SEAT_MAP.size()];
    }
    
    public int getPrice(int startStationIndex, int endStationIndex, GSeriesSeatType type) {
        return (endStationIndex - startStationIndex) * PRICE_MAP.get(type);
    }
}
