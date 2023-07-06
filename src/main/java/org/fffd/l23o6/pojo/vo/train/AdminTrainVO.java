package org.fffd.l23o6.pojo.vo.train;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.fffd.l23o6.pojo.enum_.TrainStatus;

@Data
@AllArgsConstructor
public class AdminTrainVO {
    private Long id;
    private String name;
    private String trainType;
    private Long routeId;
    private String date;
    private List<Date> departureTimes;
    private List<Date> arrivalTimes;
    private List<TrainStatus> extraInfos;
}
