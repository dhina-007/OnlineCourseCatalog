package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateLessonProgressRequest {
    private Boolean completed;
    @Min(0)
    @Max(100)
    private Integer progressPercent;
    @Min(0)
    private Integer lastPositionSec;
}
