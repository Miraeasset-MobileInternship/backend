# M-Class Backend
</br>

## Overview
M-Class Backend is the Spring Boot server for a fintech education app that helps teenagers experience simulated banking and stock investment in a classroom environment.
</br></br>
## Tech Stack
!Java
!Spring Boot
!MySQL
!Redis
!Yahoo Finance
</br></br>

## Main Features
</br>
### 1. Classroom Financial Education
- Manage classroom information such as school name, grade, class number, class currency, and class fund
- Manage students within each class
- Assign jobs to students
- Manage job-based salary, credit limits, and permission settings
- Register tradable stocks for each class

Related tables: `class`, `students`, `job`, `class_stock`
</br>
### 2. Student Money & Transaction Management

- Track each student's simulated money balance and credit score
- Record class/student balance snapshots after transactions
- Store transaction details, participants, job information, and categories
- Distinguish transfer/payment transactions through transaction categories

Related tables: `students`, `transaction_data`, `transaction_category`, `job`
</br>
### 3. Stock Market Data

- Store stock symbol, title, current price, market status, exchange name, and price movement
- Store regular market change and change percentage
- Manage trending stock data for frontend display
- Provide stock-related data to React WebView screens

Related tables: `stock_batch`, `trending_stocks`, `trending_every`
</br>
### 4. Simulated Stock Trading

- Store student stock holdings
- Track average purchase price and quantity
- Record simulated buy/sell trading history
- Connect student holdings with stock market data
- Support buy/sell flows in the React WebView

Related tables: `student_stock`, `stock_trading_data`, `stock_batch`, `class_stock`
</br>
### 5. User & Role Data

- Store basic user profile information
- Manage user role information
- Connect users with student records and classroom activities

Related tables: `user_info`, `students`, `class`
</br></br>

### Architecture
<img width="4400" height="2433" alt="systemArc" src="https://github.com/user-attachments/assets/a21892a6-7544-40dc-a960-0275460065b2" />


</br></br>
### Database Design
The original JPA entities used ID-based references such as `classId`, `studentId`, `jobId`, and `stockSymbol` instead of explicit `@ManyToOne` / `@OneToMany` mappings.  
The ERD below represents logical relationships inferred from these reference columns.
</br>
```mermaid
erDiagram
    USER_INFO ||--o{ STUDENTS : owns
    CLASSES ||--o{ STUDENTS : has
    CLASSES ||--o{ JOB : has
    CLASSES ||--o{ CLASS_STOCK : includes
    CLASSES ||--o{ TRANSACTION_DATA : records

    JOB ||--o{ STUDENTS : assigned_to
    JOB ||--o{ TRANSACTION_DATA : manager_job
    JOB ||--o{ TRANSACTION_DATA : student_job

    STUDENTS ||--o{ STUDENT_STOCK : owns
    STUDENTS ||--o{ STOCK_TRADING_DATA : trades
    STUDENTS ||--o{ TRANSACTION_DATA : participates

    TRANSACTION_CATEGORY ||--o{ TRANSACTION_DATA : categorizes

    STOCK_BATCH ||--o{ STUDENT_STOCK : referenced_by
    STOCK_BATCH ||--o{ STOCK_TRADING_DATA : traded_as
    STOCK_BATCH ||--o{ CLASS_STOCK : registered_as

    USER_INFO {
        Long id PK
        String user_name
        String phone_number
        String user_role
        Long profile_img_id
        DateTime created_at
        DateTime updated_at
    }

    CLASSES {
        Long id PK
        Long teacher_id FK
        String school_name
        String title
        int grade
        int class_number
        String currency
        int money
        DateTime created_at
        DateTime updated_at
    }

    STUDENTS {
        Long id PK
        Long class_id FK
        Long job_id FK
        Long user_id FK
        int number
        int money
        int credit_score
        DateTime created_at
        DateTime updated_at
    }

    JOB {
        Long id PK
        Long class_id FK
        String title
        int monthly_salary
        String detail
        int credit_limitation
        boolean is_withdraw_student
        boolean is_withdraw_class
        boolean is_modify_credit
        DateTime created_at
        DateTime updated_at
    }

    CLASS_STOCK {
        Long id PK
        Long class_id FK
        String isin_code
        String title
    }

    STOCK_BATCH {
        Long id PK
        String stock_symbol
        String title
        double regular_market_price
        String market_status
        String type_display
        String full_exchange_name
        String custom_price_alert_confidence
        double regular_market_change
        double regular_market_change_percent
        DateTime created_at
        DateTime updated_at
    }

    STUDENT_STOCK {
        Long id PK
        Long student_id FK
        String stock_symbol FK
        BigDecimal blended_price
        int amount
        DateTime created_at
        DateTime updated_at
    }

    STOCK_TRADING_DATA {
        Long id PK
        Long student_id FK
        String stock_symbol FK
        int amount
        int price
        boolean is_buying
        DateTime created_at
        DateTime updated_at
    }

    TRANSACTION_CATEGORY {
        Long id PK
        String title
        boolean is_transfer
        boolean is_pay
    }

    TRANSACTION_DATA {
        Long id PK
        int transaction_money
        int student_money
        int class_money
        Long manager_id FK
        Long student_id FK
        Long manager_job_id FK
        Long student_job_id FK
        Long category_id FK
        Long class_id FK
        String detail
        String from_who
        DateTime created_at
        DateTime updated_at
    }

    TRENDING_STOCKS {
        Long id PK
        String symbol
        String title
        String price
        String change_price
        String change_percent
        int change_status
        String tag_type
        String tag_market
        String tag_confidence
        boolean is_open
        DateTime created_at
        DateTime updated_at
    }

    TRENDING_EVERY {
        Long id PK
        String data_string
        DateTime created_at
        DateTime updated_at
    }
```

</br></br>

## API Overview

This section summarises the main REST APIs implemented in the backend.  
The APIs were grouped by product domain: authentication, classroom management, student/job management, banking transactions, stock trading, stock detail data, and user profile data.
</br>
> Note: The backend used a custom response wrapper, `BanklassResponseEntity`, and Swagger annotations for documenting response cases and domain-specific error codes.
</br>
---
</br>
### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/start-signin` | Sign in and return initial user/class information |
| POST | `/api/auth/start-signup` | Sign up and automatically sign in |
| POST | `/api/auth/logout` | Log out and store invalidated access token in Redis |
| POST | `/api/auth/reissue` | Reissue access token using refresh token |
| POST | `/api/auth/sendSMS` | Send phone verification code |
| GET | `/api/auth/{user_id}/get-token` | Issue access token for WebView access |
</br>
--- 
</br>
### Classroom Management
</br>
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/class/create` | Create a new class |
| GET | `/api/class/{class_id}/account` | Get class account information, including class fund and currency |
| GET | `/api/class/{class_id}/change` | Get class fund change compared to the previous transaction |
| GET | `/api/class/{class_id}/currency` | Get class currency unit |
| GET | `/api/class/{class_id}/invitation-code` | Get or issue class invitation code |
| GET | `/api/class/check/invitation-code` | Validate class invitation code |
| GET | `/api/class/enter-class/{class_id}` | Enter a class and return the student ID used in that class |
</br>
---
</br>
### Student & Job Management
</br>
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/class/{class_id}/job/all` | Get all common and class-specific jobs |
| GET | `/api/class/{class_id}/student/job/all` | Get job status of all students in a class |
| GET | `/api/class/{class_id}/student-selector` | Get student selector list for transfer/payment flows |
| GET | `/api/job/{job_id}` | Get job detail information |
| POST | `/api/job/create` | Create a new job |
| PUT | `/api/job/update` | Update one student's job |
| PUT | `/api/job/update/all` | Update jobs for multiple students |
| DELETE | `/api/job/{job_id}/delete` | Delete a job |
| POST | `/api/student/join-class` | Join a class using invitation information |
| GET | `/api/student/{student_id}/account` | Get student account information |
| GET | `/api/student/{student_id}/job` | Get student job card information |
| GET | `/api/student/{student_id}/change` | Get student balance change compared to the previous transaction |
| GET | `/api/student/salary/{student_id}` | Get salary amount based on student job |
</br>
---
</br>
### Banking & Transactions
</br>
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/transaction/student/{student_id}` | Get student transaction history with type filter and pagination |
| GET | `/api/transaction/class/{class_id}` | Get class account transaction history with type filter and pagination |
| GET | `/api/transaction/category` | Get transaction category selector list |
| POST | `/api/transaction/transfer` | Transfer money from student account to class account |
| POST | `/api/transaction/pay` | Pay money from class account to student account |
| GET | `/api/transaction/student/detail/{transaction_id}` | Get transaction detail from student account perspective |
| GET | `/api/transaction/class/detail/{transaction_id}` | Get transaction detail from class account perspective |
</br>
---
</br>
### Stock Portfolio & Trading
</br>
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/stock/total-info/{student_id}` | Get student's total stock portfolio summary |
| GET | `/api/stock/{student_id}` | Get student's owned stock list with pagination |
| GET | `/api/stock/trending` | Get today's trending stock list |
| GET | `/api/stock/search-stocks` | Get stock search autocomplete result |
| GET | `/api/stock/search-stocks-list` | Get stored search autocomplete list |
| GET | `/api/stock/news/market` | Get market news |
| GET | `/api/stock/check-buying` | Check stock price and required information before buying |
| POST | `/api/stock/buy` | Create simulated stock buy order |
| GET | `/api/stock/check-selling` | Check sellable amount and current price before selling |
| POST | `/api/stock/sell` | Create simulated stock sell order |
</br>
---
</br>
### Stock Detail Data
</br>
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/stock-detail/{stock_id}` | Get stock detail information |
| GET | `/api/stock-detail/{stock_id}/similar` | Get similar stock list |
| GET | `/api/stock-detail/{stock_id}/news` | Get stock-related news |
| GET | `/api/stock-detail/{stock_id}/recommend-trend` | Get recommendation trend graph data |
| GET | `/api/stock-detail/{stock_id}/chart` | Get chart data by range |
| GET | `/api/stock-detail/{stock_id}/company-info` | Get listed company information |
| GET | `/api/stock-detail/{stock_id}/stock-info` | Get stock summary information |
| GET | `/api/stock-detail/watch-list` | Get most watched / ranked stock list |
</br>
---
</br>
### User Profile
</br>
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/user/profile-img-list` | Get available profile image list |
| GET | `/api/user/{user_id}/header-info` | Get user name and profile image for header UI |
| GET | `/api/user/{user_id}/student/join-class-list` | Get all classes joined by the user as a student |
</br>
## Redis Usage
</br>
Redis was used to manage short-lived and temporary data that did not need to be stored permanently in MySQL.</br>

The project used Redis for authentication-related token handling, phone verification codes, class invitation codes, Yahoo Finance API error logs, and search query logs.</br>
</br>
| Redis Repository | Purpose | Data Type |
|---|---|---|
| `LogoutAccessTokenRedisRepository` | Stores logged-out access tokens to prevent reused tokens after logout | Authentication / token blacklist |
| `PhoneNumberCodeRedisRepository` | Stores temporary phone verification codes during sign-up | SMS verification |
| `ClassInvitationCodeRedisRepository` | Stores invitation codes used when inviting students to a class | Class invitation |
| `YhFinanceErrorLogRedisRepository` | Stores Yahoo Finance API error logs for temporary tracking/debugging | External API error log |
| `SearchQueryLogRedisRepository` | Stores search query logs for later inspection | Search / query log |
</br>
```mermaid
flowchart TD
    A[Spring Boot Backend] --> B[Redis]

    B --> C[Authentication Token Data]
    C --> C1[Logout Access Token]
		C --> C2[Token Reissue-related Data]

    B --> D[Verification Data]
    D --> D1[Phone Number Verification Code]
    D --> D2[Class Invitation Code]

    B --> E[Temporary Operational Logs]
    E --> E1[Yahoo Finance API Error Log]
    E --> E2[Search Query Log]
```
</br></br>
## External API Integration

Yahoo Finance API was integrated to provide stock-related data for the investment education flow.</br>

The backend retrieved and processed data such as:</br>

- Current stock price
- Chart data
- Stock news
- Company information
- Related stock data
- Ranking data
</br>
The processed data was then provided to the React WebView through backend REST APIs.
</br></br>
## Current Project Status
This backend was originally built as part of an internship project and was not publicly deployed. Some environment-specific configuration, database setup, or API credentials may need to be restored before running the full application locally.
</br></br>

## Future Improvements

- Add Docker Compose for easier MySQL and Redis setup
- Add seed data for local demo mode
- Add unit and integration tests for core APIs
- Refactor ID-based entity references into explicit JPA relationships where appropriate
- Separate external stock data provider logic to make Yahoo Finance API replaceable
- Improve error handling and validation for simulated trading APIs
