package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;


public class KSeriesSeatStrategy extends TrainSeatStrategy {
    public static final KSeriesSeatStrategy INSTANCE = new KSeriesSeatStrategy();
     
    private final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SOFT_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> NO_SEAT_MAP = new HashMap<>();

    private final Map<KSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT, SOFT_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT, HARD_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.SOFT_SEAT, SOFT_SEAT_MAP);
        put(KSeriesSeatType.HARD_SEAT, HARD_SEAT_MAP);
        put(KSeriesSeatType.NO_SEAT, NO_SEAT_MAP);
    }};
    
    private final Map<KSeriesSeatType, Integer> PRICE_MAP = new HashMap<>() {
        {
            put(KSeriesSeatType.SOFT_SLEEPER_SEAT, 100);
            put(KSeriesSeatType.HARD_SLEEPER_SEAT, 50);
            put(KSeriesSeatType.SOFT_SEAT, 20);
            put(KSeriesSeatType.HARD_SEAT, 10);
            put(KSeriesSeatType.NO_SEAT, 5);
        }
    };
    
    private KSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("软卧1号上铺", "软卧2号下铺", "软卧3号上铺", "软卧4号上铺", "软卧5号上铺", "软卧6号下铺", "软卧7号上铺", "软卧8号上铺")) {
            SOFT_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("硬卧1号上铺", "硬卧2号中铺", "硬卧3号下铺", "硬卧4号上铺", "硬卧5号中铺", "硬卧6号下铺", "硬卧7号上铺", "硬卧8号中铺", "硬卧9号下铺", "硬卧10号上铺", "硬卧11号中铺", "硬卧12号下铺")) {
            HARD_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("1车1座", "1车2座", "1车3座", "1车4座", "1车5座", "1车6座", "1车7座", "1车8座", "2车1座", "2车2座", "2车3座", "2车4座", "2车5座", "2车6座", "2车7座", "2车8座")) {
            SOFT_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("3车1座", "3车2座", "3车3座", "3车4座", "3车5座", "3车6座", "3车7座", "3车8座", "3车9座", "3车10座", "4车1座", "4车2座", "4车3座", "4车4座", "4车5座", "4车6座", "4车7座", "4车8座", "4车9座", "4车10座")) {
            HARD_SEAT_MAP.put(counter++, s);
        }
        
        for (String s: Arrays.asList("无座1", "无座2", "无座3", "无座4", "无座5", "无座6", "无座7", "无座8", "无座9", "无座10", "无座11", "无座12", "无座13", "无座14", "无座15")) {
            NO_SEAT_MAP.put(counter++, s);
        }
    }

    public enum KSeriesSeatType implements SeatType {
        SOFT_SLEEPER_SEAT("软卧"), HARD_SLEEPER_SEAT("硬卧"), SOFT_SEAT("软座"), HARD_SEAT("硬座"), NO_SEAT("无座");
        private String text;
        KSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static KSeriesSeatType fromString(String text) {
            for (KSeriesSeatType b : KSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, KSeriesSeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        int startIndex = switch (type) {
            case HARD_SLEEPER_SEAT -> SOFT_SLEEPER_SEAT_MAP.size();
            case SOFT_SEAT -> SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size();
            case HARD_SEAT -> SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size();
            case NO_SEAT -> SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size();
            default -> 0;
        };
        
        for (int i = startIndex; i < startIndex + TYPE_MAP.get(type).size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
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

    public Map<KSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        int softSleeperSeatCount = 0;
        int hardSleeperSeatCount = 0;
        int softSeatCount = 0;
        int hardSeatCount = 0;
        int noSeatCount = 0;
        
        for (int i = 0; i < SOFT_SLEEPER_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    softSleeperSeatCount++;
            }
        }
        
        for (int i = SOFT_SLEEPER_SEAT_MAP.size(); i < SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    hardSleeperSeatCount++;
            }
        }
        
        for (int i = SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size(); i < SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    softSeatCount++;
            }
        }
        
        for (int i = SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size(); i < SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    hardSeatCount++;
            }
        }
        
        for (int i = SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size(); i < SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size() + NO_SEAT_MAP.size(); i++) {
            for (int j = startStationIndex; j < endStationIndex; j++) {
                if (seatMap[j][i])
                    break;
                if (j == endStationIndex - 1)
                    noSeatCount++;
            }
        }

        return Map.of(KSeriesSeatType.SOFT_SLEEPER_SEAT, softSleeperSeatCount, KSeriesSeatType.HARD_SLEEPER_SEAT, hardSleeperSeatCount, KSeriesSeatType.SOFT_SEAT, softSeatCount, KSeriesSeatType.HARD_SEAT, hardSeatCount, KSeriesSeatType.NO_SEAT, noSeatCount);
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size() + NO_SEAT_MAP.size()];
    }
    
    public int getPrice(int startStationIndex, int endStationIndex, KSeriesSeatType type) {
        return (endStationIndex - startStationIndex) * PRICE_MAP.get(type);
    }
}
