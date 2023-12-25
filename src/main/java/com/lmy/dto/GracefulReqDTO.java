package com.lmy.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author mingyang.lu
 * @date 2023/12/18 16:23
 */
@Data
public class GracefulReqDTO {

    @NotBlank(message = "not blank")
    private String id;
}
