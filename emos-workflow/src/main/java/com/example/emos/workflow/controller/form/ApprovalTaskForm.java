package com.example.emos.workflow.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ApprovalTaskForm {

    @NotBlank(message = "taskId不能为空")
    private String taskId;

    @NotBlank(message = "approval不能为空")
    @Pattern(regexp = "^同意$|^不同意$", message = "approval内容不正确")
    private String approval;

//    @NotBlank(message = "code不能为空")
    private String code;

//    @NotBlank(message = "tcode不能为空")
//    @Pattern(regexp = "^[0-9]{6}$",message = "tcode必须是6位数字")
    private String tcode;

    @NotBlank(message = "instanceId不能为空")
    @Pattern(regexp = "^[0-9A-Za-z\\-]{36}$", message = "instanceId内容不正确")
    private String instanceId;

    @NotBlank(message = "variety不能为空")
    @Pattern(regexp = "^员工请假$|^会议申请$|^报销申请$", message = "variety内容不正确")
    private String variety;

    private String reject;

}
