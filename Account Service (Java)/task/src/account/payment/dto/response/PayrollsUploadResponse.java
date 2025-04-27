package account.payment.dto.response;

public record PayrollsUploadResponse(String status) {
    public static PayrollsUploadResponse succeed() {
        return new PayrollsUploadResponse("Added successfully!");
    }
}
