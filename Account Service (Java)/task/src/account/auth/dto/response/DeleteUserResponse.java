package account.auth.dto.response;

public record DeleteUserResponse(String user, // User email
                                 String status) {
    public static DeleteUserResponse succeed(String email) {
        return new DeleteUserResponse(email, "Deleted successfully!");
    }
}
