## Stage 5: The authorization

- [x] Create `groups` table
    - [x] Create `Many-to-Many` relationship between `Group` and `User`
- [x] Initialize user roles within the database
- [x] First user has `ROLE_ADMINISTRATOR` role and the rest has `ROLE_USER`
- [x] Authorization violation responds with `HTTP Forbidden status 403`
- [x] Update `POST api/auth/signup`
    - Response: Status 2s00 and the body with the user information
- [x] Authorize `GET api/empl/payment`
    - Only authenticated users (except admin)
- [x] Authorize `POST api/acct/payment` and `PUT api/acct/payment`
    - Only accountants
- [x] Create endpoints for admin-only
    - [x] `GET api/admin/user`
    - [x] `DELETE api/admin/user`
        - Admin cannot delete himself (return bad request 400 with a message)
    - [x] `PUT api/admin/user/role`
        - Request role does not exist
        - Remove the role not included in user roles
        - Grant business role to admin and vice versa
- [x] Create custom `ResourceNotFoundException` & handling method for it

## Stage 6: Logging Events

- [x] Endpoint to retrieve all security events in ascending order by ID (`api/security/events`)
    - [x] Implement `SecurityEvent` entity class
    - [x] Implement the persistence solution for those security events (save in the database)
    - [x] Implement the logic to persist the security events to the database
- [x] Add role `auditor` (a business group)
    - [x] Update the role model
- [x] If there are more than five consecutive attempts to enter the incorrect password
    - log as brute force attack event
    - block the user account
    - [x] Implement the `LoginAttemptService` contains methods to track failed login attempts
        - Increase the number of attempts
        - Publish `BruteForceEvent` if the failed attempts counter reach the maximum number
            - Block the user account
        - Reset the counter if the login is successful
- [x] Implement administrative endpoint to unlock user: `api/admin/user/access`
