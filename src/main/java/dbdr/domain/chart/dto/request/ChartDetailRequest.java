package dbdr.domain.chart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChartDetailRequest(
            @Schema(description = "상태", example = "오늘은 상태가 매우 좋으셨다.")
            String conditionDisease,
            @Schema(description = "돌봄대상자", example = "1")
            Long recipientId,
            @Schema(description = "신체활동", example = "{\"wash\": true, \"bath\": true, \"mealType\": \"채식\","
                + "\"intakeAmount\": \"3회\", \"physicalRestroom\": \"6\", \"hasWalked\": false,"
                + "\"positionChangeRequired\": false, \"mobilityAssistance\": false, "
                + "\"physicalNote\": \"평소보다 컨디션이 좋으셨다.\"}")
            BodyManagementRequest bodyManagement,
            @Schema(description = "간호관리", example = "{\"systolic\": \"300\","
                + "\"diastolic\": \"200\","
                + "\"healthTemperature\": \"32도\","
                + "\"healthCareProvided\": true,"
                + "\"nursingCareProvided\": true,"
                + "\"emergencyCareProvided\": true,"
                + "\"healthNote\": \"없음\"}")
            NursingManagementRequest nursingManagement,
            @Schema(description = "인지관리", example = "{\"cognitiveHelp\": true,"
                + "\"companionshipProvided\": true,"
                + "\"cognitiveNote\": \"없음\"}")
            CognitiveManagementRequest cognitiveManagement,
            @Schema(description = "기능회복훈련", example = "{\"recoveryProgram\": \"회복\","
                + "\"recoveryTraining\": true,"
                + "\"cognitiveTrainingProvided\": true,"
                + "\"physicalTherapyProvided\": true,"
                + "\"recoveryNote\": \"없음\"}")
            RecoveryTrainingRequest recoveryTraining
) {
}
