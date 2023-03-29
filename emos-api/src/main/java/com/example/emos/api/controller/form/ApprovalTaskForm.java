package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "审批任务表单")
public class ApprovalTaskForm {

    @NotBlank(message = "taskId不能为空")
    @Schema(description = "任务ID")
    private String taskId;

    @NotBlank(message = "approval不能为空")
    @Pattern(regexp = "^同意$|^不同意$", message = "approval内容不正确")
    @Schema(description = "审批意见")
    private String approval;

    @NotBlank(message = "instanceId不能为空")
    @Pattern(regexp = "^[0-9A-Za-z\\-]{36}$", message = "instanceId内容不正确")
    private String instanceId;

    @NotBlank(message = "variety不能为空")
    @Pattern(regexp = "^员工请假$|^会议申请$|^报销申请$", message = "variety内容不正确")
    private String variety;

    private String reject;
}
