# Flow Login

Tai lieu nay chot lai ranh gioi trach nhiem giua frontend va backend cho luong dang nhap cua `dialo-desktop`.

Muc tieu:

- backend xu ly nghiep vu dang nhap
- frontend chi lam UI flow, luu token, bootstrap du lieu sau dang nhap
- hai ben thong nhat ro request, response, loading state, va dieu kien duoc vao app

## 1. Nguyen tac

Backend phu trach:

- xac thuc `phone + password`
- kiem tra tai khoan co bi khoa hay khong
- tao `accessToken`
- tao `refreshToken`
- tao `sessId`
- quan ly refresh token va session tren server

Frontend phu trach:

- thu thap input tu form login
- validate local o muc UI toi thieu
- goi API `signin`
- luu token tam thoi khi signin thanh cong
- hien loading trong luc bootstrap du lieu sau dang nhap
- goi cac API can thiet bang access token
- chi vao app khi bootstrap du lieu thanh cong

Frontend khong duoc tu xac dinh nghiep vu dang nhap thanh cong chi dua tren `signin`.

## 2. Routes trong flow

- `POST /api/v1/auth/signin`
- `GET /api/v1/me`
- `POST /api/v1/auth/token/refresh`
- `POST /api/v1/auth/signout`

## 3. Dinh nghia "dang nhap thanh cong"

Dang nhap thanh cong chi khi ca 2 buoc sau deu thanh cong:

1. `POST /api/v1/auth/signin` tra ve token hop le
2. frontend dung token do goi bootstrap API toi thieu la `GET /api/v1/me` va nhan duoc current user

Neu `signin` thanh cong nhung `GET /me` that bai, frontend khong duoc vao app.

## 4. Flow chuan

### 4.1 User submit login form

Frontend gui:

`POST /api/v1/auth/signin`

```json
{
  "phone": "0912345678",
  "password": "your-password"
}
```

Backend tra toi thieu:

```json
{
  "status": 200,
  "message": "Dang nhap thanh cong",
  "data": {
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token",
    "tokenType": "Bearer",
    "sessId": "session-id"
  }
}
```

Sau khi nhan response `signin` thanh cong, frontend:

1. luu tam `accessToken`, `refreshToken`, `sessId`
2. bat loading bootstrap
3. chua vao app ngay
4. goi `GET /api/v1/me`

### 4.2 Bootstrap du lieu sau dang nhap

Frontend gui:

`GET /api/v1/me`

```http
Authorization: Bearer <accessToken>
```

Backend tra toi thieu:

```json
{
  "data": {
    "id": "user-id",
    "userName": "Dialo User",
    "bio": "...",
    "gender": "MALE",
    "dob": "2005-12-29",
    "avatar": "https://...",
    "background": "https://..."
  }
}
```

Frontend xu ly:

1. map du lieu backend sang user model cua UI
2. set current user
3. tat loading bootstrap
4. set authenticated state
5. dieu huong vao app

Luu y:

- `GET /api/v1/me` la bootstrap API toi thieu
- sau nay co the mo rong them `friends`, `conversations`, `settings`, nhung quy tac van giu nguyen: phai bootstrap xong moi vao app

### 4.3 Hien thi loading

Frontend can co loading state ro rang trong 2 truong hop:

- dang submit `signin`
- da co token va dang bootstrap du lieu sau dang nhap

Man hinh loading nay chi la UI state. No khong chua nghiep vu dang nhap.

### 4.4 App mo lai khi da co session local

Khi app mo:

1. frontend doc `accessToken`, `refreshToken`, `sessId` tu local storage
2. neu khong co session thi o trang thai guest
3. neu co session thi hien loading bootstrap
4. frontend goi `GET /api/v1/me`

Neu `GET /me` thanh cong:

- hydrate current user
- tat loading
- vao app

Neu `GET /me` that bai vi access token het han hoac khong hop le:

frontend goi:

`POST /api/v1/auth/token/refresh`

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

Backend tra toi thieu:

```json
{
  "status": 200,
  "message": "Da lam moi token",
  "data": {
    "accessToken": "new-access-token",
    "tokenType": "Bearer"
  }
}
```

Neu backend van tra them `refreshToken` moi thi frontend cap nhat theo response. Neu backend khong rotate refresh token thi frontend giu refresh token cu.

Sau refresh thanh cong:

1. frontend cap nhat token local
2. goi lai `GET /api/v1/me`
3. neu thanh cong thi vao app

Neu refresh that bai:

1. frontend xoa session local
2. tat loading
3. quay ve `/login`

## 5. Logout thiet bi hien tai

Frontend gui:

`POST /api/v1/auth/signout`

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

```json
{
  "refreshToken": "jwt-refresh-token",
  "sessId": "session-id"
}
```

Backend tra toi thieu:

```json
{
  "status": 201,
  "message": "Ban da dang xuat thiet bi nay"
}
```

Frontend xu ly:

1. goi signout
2. du request thanh cong hay that bai, van clear session local neu user da bam logout thu cong
3. clear current user
4. quay ve `/login`

## 6. Loi can thong nhat

### 6.1 Signin validation fail

Backend nen tra:

```json
{
  "status": 400,
  "message": "Du lieu gui len khong hop le",
  "errors": [
    {
      "field": "phone",
      "defaultMessage": "So dien thoai khong hop le"
    },
    {
      "field": "password",
      "defaultMessage": "Mat khau khong duoc de trong"
    }
  ]
}
```

Frontend chi co nhiem vu map loi nay len form.

### 6.2 Invalid credentials

Backend nen tra:

```json
{
  "status": 401,
  "message": "So dien thoai hoac mat khau khong chinh xac"
}
```

Frontend hien message loi chung, khong luu session.

### 6.3 Account locked

Backend nen tra:

```json
{
  "status": 423,
  "message": "Tai khoan cua ban da bi khoa"
}
```

Frontend hien message loi chung, khong luu session.

## 7. State toi thieu phia frontend

Frontend can luu:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "sessId": "session-id"
}
```

Va current user da duoc hydrate:

```json
{
  "id": "user-id",
  "displayName": "Dialo User",
  "birthDay": "29",
  "birthMonth": "12",
  "birthYear": "2005",
  "gender": "Nam",
  "bio": "",
  "avatarUrl": "https://...",
  "backgroundUrl": "https://..."
}
```

## 8. Tieu chi chap nhan

Flow nay duoc coi la dung khi:

- frontend khong tu xu ly nghiep vu xac thuc
- backend quyet dinh `signin` thanh cong hay that bai
- frontend chi vao app sau khi bootstrap data thanh cong
- frontend co loading state ro rang trong luc cho bootstrap
- refresh khong bat buoc rotate refresh token
- logout van xoa session local ngay ca khi request signout fail

## 9. Pseudoflow tong quat

```text
User nhap phone/password
-> frontend POST /auth/signin
-> neu fail: hien loi
-> neu success: luu token tam thoi, bat loading
-> frontend GET /me
-> neu /me success: set current user, tat loading, vao app
-> neu /me fail vi auth: frontend POST /auth/token/refresh
-> neu refresh success: cap nhat token, goi lai /me
-> neu refresh fail: clear session, tat loading, ve login
```

## 10. Sequence chi tiet

### 10.1 Login thanh cong

```text
User
-> Frontend: nhap phone + password
-> Frontend: validate local o muc UI toi thieu

Frontend
-> API Gateway: POST /api/v1/auth/signin
   Authorization: none
   Body: { phone, password }

API Gateway
-> Identity: forward request /api/v1/auth/signin

Identity
-> Identity: validate request body
-> Identity: tim account theo phone
-> Identity: kiem tra password
-> Identity: kiem tra account co bi khoa khong
-> Identity: doc User-Agent + IP
-> Identity: revoke refresh token cu cua cung device neu co
-> Identity: tao accessToken
-> Identity: tao refreshToken
-> Identity: luu refresh token
-> Identity: tao ClientSession va sinh sessId

Identity
-> API Gateway: 200 OK
   data: { accessToken, refreshToken, tokenType, sessId }

API Gateway
-> Frontend: tra response signin

Frontend
-> Frontend: luu accessToken + refreshToken + sessId tam thoi
-> Frontend: hien loading bootstrap
-> Frontend: chua vao app ngay

Frontend
-> API Gateway: GET /api/v1/me
   Authorization: Bearer <accessToken>

API Gateway
-> API Gateway: JwtFilter validate access token
-> API Gateway: lay subject tu token
-> API Gateway: gan header X-User-Id
-> Social Graph: forward request /api/v1/me + X-User-Id

Social Graph
-> Social Graph: lay current user tu X-User-Id
-> Social Graph: doc ho so user

Social Graph
-> API Gateway: 200 OK
   data: { id, userName, bio, gender, dob, avatar, background, ... }

API Gateway
-> Frontend: tra response /me

Frontend
-> Frontend: map du lieu user cho UI
-> Frontend: set current user
-> Frontend: tat loading bootstrap
-> Frontend: set isAuthenticated = true
-> Frontend: dieu huong vao app
```

### 10.2 Login that bai do du lieu khong hop le

```text
Frontend
-> API Gateway: POST /api/v1/auth/signin

API Gateway
-> Identity: forward request

Identity
-> Identity: validate request body fail

Identity
-> API Gateway: 400 Bad Request
   message: Du lieu gui len khong hop le
   errors: [{ field, defaultMessage }]

API Gateway
-> Frontend: tra loi validation

Frontend
-> Frontend: map loi vao field form
-> Frontend: khong luu session
```

### 10.3 Login that bai do sai tai khoan hoac mat khau

```text
Frontend
-> API Gateway: POST /api/v1/auth/signin

API Gateway
-> Identity: forward request

Identity
-> Identity: tim account theo phone
-> Identity: neu khong co account -> invalid credentials
hoac
-> Identity: neu sai password -> invalid credentials

Identity
-> API Gateway: 401 Unauthorized
   message: So dien thoai hoac mat khau khong chinh xac

API Gateway
-> Frontend: tra loi dang nhap

Frontend
-> Frontend: hien loi chung
-> Frontend: khong luu session
```

### 10.4 Login that bai do tai khoan bi khoa

```text
Frontend
-> API Gateway: POST /api/v1/auth/signin

API Gateway
-> Identity: forward request

Identity
-> Identity: xac thuc dung password
-> Identity: kiem tra account locked = true

Identity
-> API Gateway: 423 Locked
   message: Tai khoan cua ban da bi khoa

API Gateway
-> Frontend: tra loi

Frontend
-> Frontend: hien thong bao khoa tai khoan
-> Frontend: khong luu session
```

### 10.5 App mo lai khi da co session local

```text
Frontend
-> Frontend: doc accessToken + refreshToken + sessId tu local storage
-> Frontend: hien loading bootstrap

Frontend
-> API Gateway: GET /api/v1/me
   Authorization: Bearer <accessToken>

API Gateway
-> API Gateway: validate access token

alt access token con hieu luc
  API Gateway
  -> Social Graph: forward /api/v1/me + X-User-Id
  Social Graph
  -> API Gateway: 200 OK user profile
  API Gateway
  -> Frontend: 200 OK
  Frontend
  -> Frontend: hydrate user
  -> Frontend: tat loading
  -> Frontend: vao app
else access token het han hoac khong hop le
  API Gateway
  -> Frontend: loi auth
  Frontend
  -> API Gateway: POST /api/v1/auth/token/refresh
     Body: { refreshToken }

  API Gateway
  -> Identity: forward request refresh

  Identity
  -> Identity: decode refresh token
  -> Identity: tim token trong storage
  -> Identity: so khop token hash

  alt refresh token hop le
    Identity
    -> Identity: tao accessToken moi
    -> API Gateway: 200 OK
       data: { accessToken, tokenType }
    API Gateway
    -> Frontend: response refresh thanh cong
    Frontend
    -> Frontend: cap nhat token local
    -> API Gateway: goi lai GET /api/v1/me
    API Gateway
    -> Social Graph: forward /api/v1/me
    Social Graph
    -> API Gateway: 200 OK user profile
    API Gateway
    -> Frontend: 200 OK
    Frontend
    -> Frontend: hydrate user
    -> Frontend: tat loading
    -> Frontend: vao app
  else refresh token khong hop le hoac khong con ton tai
    Identity
    -> API Gateway: loi auth
    API Gateway
    -> Frontend: refresh fail
    Frontend
    -> Frontend: clear session local
    -> Frontend: tat loading
    -> Frontend: ve /login
end
```

### 10.6 Logout thiet bi hien tai

```text
User
-> Frontend: bam logout

Frontend
-> API Gateway: POST /api/v1/auth/signout
   Authorization: Bearer <accessToken>
   Body: { refreshToken, sessId }

API Gateway
-> API Gateway: validate access token
-> API Gateway: gan ngu canh user
-> Identity: forward request signout

Identity
-> Identity: lay user hien tai tu access token
-> Identity: decode jti tu refresh token
-> Identity: kiem tra refresh token co thuoc user hien tai khong
-> Identity: revoke refresh token
-> Identity: doi trang thai ClientSession sang LOGGED_OUT

Identity
-> API Gateway: 201 Created
   message: Ban da dang xuat thiet bi nay

API Gateway
-> Frontend: tra response signout

Frontend
-> Frontend: clear accessToken + refreshToken + sessId
-> Frontend: clear current user
-> Frontend: ve /login
```

## 11. Provisioning voi Outbox

Sau khi bo sung outbox, luong tao user va login duoc chot nhu sau:

1. `POST /api/v1/auth/signup` thanh cong khi `identity` da ghi duoc trong cung transaction:
   - account
   - credential
   - outbox event `USER_CREATED`
2. worker outbox cua `identity` doc event chua publish va day len RabbitMQ voi routing key `user.created`
3. `social-graph` consume `user.created` theo kieu idempotent:
   - neu user chua ton tai thi tao ho so mac dinh
   - neu user da ton tai thi bo qua tao lai
4. sau khi dam bao ho so user da san sang, `social-graph` publish event `user.profile.provisioned`
5. `identity` consume event ACK nay va danh dau account `profileProvisioned = true`
6. chi sau buoc 5 thi `signin` moi duoc phat token thanh cong

He qua nghiep vu:

- `messaging/chat` van co the consume `user.created` binh thuong
- `signin` khong con gap truong hop phat token xong ma `GET /me` chua co du lieu
- neu user bam login qua som, backend phai tra loi ro rang rang tai khoan dang duoc khoi tao

Backend tra khi account chua duoc provision xong:

```json
{
  "status": 409,
  "message": "Tai khoan dang duoc khoi tao. Vui long thu lai sau vai giay"
}
```

Frontend xu ly:

- khong luu session
- hien thong bao dang khoi tao tai khoan
- cho phep user thu dang nhap lai sau vai giay