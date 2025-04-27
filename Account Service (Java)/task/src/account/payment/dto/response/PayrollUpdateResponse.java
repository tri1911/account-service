package account.payment.dto.response;

public record PayrollUpdateResponse(String status) {
    public static PayrollUpdateResponse succeed() {
        return new PayrollUpdateResponse("Updated successfully!");
    }
}
