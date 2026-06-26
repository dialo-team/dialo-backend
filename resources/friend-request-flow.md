# Flow ket ban

Tai lieu nay chot lai flow ket ban giua frontend va backend theo code hien tai cua `social-graph`.

Tai lieu lien quan:
- `resources/conversation-realtime-flow.md`: flow nhan tin direct, system event khi tro thanh ban be, va realtime conversation list.

## 1. API backend dung trong flow

- `GET /api/v1/users/phone/{phone}/info`
- `GET /api/v1/users/{targetId}/info`
- `GET /api/v1/users/{targetId}/check`
- `POST /api/v1/users/{targetId}/request`
- `DELETE /api/v1/users/{targetId}/request`
- `POST /api/v1/users/{targetId}/accept`
- `POST /api/v1/users/{targetId}/reject`
- `DELETE /api/v1/users/{targetId}/unfriend`
- `GET /api/v1/me/friend-requests`
- `GET /api/v1/me/friend-requests/sent`

## 2. Nguon su that de render UI

Frontend uu tien dung response tu backend de render trang thai va nut hanh dong.

Can doc cac truong sau:
- `relationStatus`
- `isSelf`
- `canAddFriend`
- `canCancelRequest`
- `canAcceptRequest`
- `canRejectRequest`
- `canMessage`
- `canCall`
- `canUnfriend`

Khong nen hard-code nut chi dua vao text trang thai neu backend da tra ve cac co `can*`.

## 3. Tim kiem theo so dien thoai

### 3.1. Mo modal tim kiem
- Nguoi dung bam nut `Tim kiem`.
- Frontend hien thi modal nhap so dien thoai.

### 3.2. Thuc hien tim kiem

Request:

`GET /api/v1/users/phone/{phone}/info`

Header:

```http
Authorization: Bearer <accessToken>
```

Response toi thieu:

```json
{
  "data": {
    "id": "user-id",
    "userName": "Dialo User",
    "bio": "...",
    "avatar": "https://...",
    "background": "https://...",
    "theme": "default",
    "relationStatus": "NONE",
    "isSelf": false,
    "canAddFriend": true,
    "canCancelRequest": false,
    "canAcceptRequest": false,
    "canRejectRequest": false,
    "canMessage": true,
    "canCall": false,
    "canUnfriend": false
  }
}
```

Frontend co the dung ngay response nay de render nut hanh dong. Khong bat buoc goi them `check` neu da co du thong tin.

### 3.3. Quy tac hien thi ket qua tim kiem

#### Truong hop A: Tai khoan cua toi

Dieu kien:
- `relationStatus = SELF`
- `isSelf = true`

UI:
- hien thi thong tin tai khoan cua toi
- khong hien thi nut `Ket ban`
- khong hien thi nut `Nhan tin`
- khong hien thi nut `Goi dien`
- khong hien thi nut `Xoa khoi danh sach ban be`

#### Truong hop B: Chua ket ban

Dieu kien:
- `relationStatus = NONE`
- `canAddFriend = true`

UI:
- hien thi thong tin tai khoan
- hien thi nut `Ket ban`
- neu `canMessage = true` thi hien thi nut `Nhan tin`

#### Truong hop C: Da gui loi moi ket ban

Dieu kien:
- `relationStatus = OUTGOING_REQUEST`
- `canCancelRequest = true`

UI:
- hien thi thong tin tai khoan
- hien thi nut `Huy yeu cau`
- neu `canMessage = true` thi hien thi nut `Nhan tin`

#### Truong hop D: Da nhan loi moi ket ban tu doi phuong

Dieu kien:
- `relationStatus = INCOMING_REQUEST`
- `canAcceptRequest = true`
- `canRejectRequest = true`

UI:
- hien thi thong tin tai khoan
- hien thi nut `Chap nhan`
- hien thi nut `Tu choi`
- neu `canMessage = true` thi hien thi nut `Nhan tin`

#### Truong hop E: Da la ban be

Dieu kien:
- `relationStatus = FRIEND`

UI:
- hien thi thong tin tai khoan
- neu `canCall = true` thi hien thi nut `Goi dien`
- neu `canMessage = true` thi hien thi nut `Nhan tin`
- neu `canUnfriend = true` thi hien thi nut `Xoa khoi danh sach ban be`

#### Truong hop F: Dang bi block

Dieu kien:
- `relationStatus = BLOCKED` hoac `blocked = true`

UI:
- hien thi thong tin tai khoan neu backend cho phep
- khong hien thi nut `Ket ban`
- khong cho gui loi moi ket ban

## 4. Bang mapping trang thai va hanh dong

| relationStatus | Y nghia | Nut nen hien thi |
| --- | --- | --- |
| `SELF` | Tai khoan cua toi | khong hien thi `Ket ban`, `Nhan tin`, `Goi dien`, `Xoa khoi danh sach ban be` |
| `NONE` | Chua ket ban | `Ket ban`, `Nhan tin` neu `canMessage = true` |
| `OUTGOING_REQUEST` | Toi da gui loi moi | `Huy yeu cau`, `Nhan tin` neu `canMessage = true` |
| `INCOMING_REQUEST` | Doi phuong da gui loi moi cho toi | `Chap nhan`, `Tu choi`, `Nhan tin` neu `canMessage = true` |
| `FRIEND` | Da la ban be | `Goi dien`, `Nhan tin`, `Xoa khoi danh sach ban be` theo cac co `can*` |
| `BLOCKED` | Mot trong hai ben dang block | khong cho thao tac ket ban |

## 5. Nhap tin voi nguoi chua la ban be

### 5.1. Dieu kien duoc nhan tin
- Nguoi dung tim thay doi phuong qua so dien thoai.
- Khong co block tu doi phuong lam mat quyen nhan tin.
- Backend tra `canMessage = true`.

### 5.2. Quy tac hien thi nut `Nhan tin`
- Neu `canMessage = true` thi frontend hien thi nut `Nhan tin`.
- Quy tac nay ap dung cho ca:
  - `NONE`
  - `OUTGOING_REQUEST`
  - `INCOMING_REQUEST`
  - `FRIEND`
- Neu `canMessage = false` thi khong hien thi nut `Nhan tin`.

### 5.3. Nghiep vu
- Nguoi dung co the nhan tin truoc, sau do moi ket ban.
- Viec da co hoac chua co quan he ban be khong duoc tu dong chan viec mo direct conversation, neu backend da cho phep `canMessage = true`.

## 6. Gui loi moi ket ban

### 6.1. Dieu kien hien thi nut
- `canAddFriend = true`
- thong thuong tuong ung voi `relationStatus = NONE`

### 6.2. Flow UI
- Nguoi dung bam nut `Ket ban`.
- Frontend chuyen sang man hinh hoac modal `Viet ly do ket ban`.
- Nguoi dung nhap noi dung ly do.
- Nguoi dung bam nut `Gui`.

### 6.3. Request

`POST /api/v1/users/{targetId}/request`

```json
{
  "reason": "Xin chao, minh muon ket ban"
}
```

### 6.4. Nghiep vu backend
- xac minh sender ton tai
- xac minh receiver ton tai
- khong cho gui cho chinh minh
- neu dang co block giua hai ben thi reject
- neu da co quan he `PENDING` hoac `ACCEPTED` thi reject
- neu hop le thi tao `FRIENDSHIP` moi voi:
  - `status = PENDING`
  - `source = DIRECT`
  - `reason = request.reason`

### 6.5. Response
- `200 OK`
- body rong

### 6.6. Hanh vi frontend sau khi thanh cong
- cap nhat UI local thanh `OUTGOING_REQUEST`
- doi nut `Ket ban` thanh `Huy yeu cau`
- co the goi lai `GET /api/v1/users/{targetId}/check` neu can dong bo lai

## 7. Huy loi moi ket ban

### 7.1. Dieu kien hien thi nut
- `canCancelRequest = true`
- thong thuong tuong ung voi `relationStatus = OUTGOING_REQUEST`

### 7.2. Request

`DELETE /api/v1/users/{targetId}/request`

### 7.3. Nghiep vu backend
- chi requester moi duoc huy
- chi huy khi quan he dang `PENDING`

### 7.4. Response
- `200 OK`
- body rong

### 7.5. Hanh vi frontend sau khi thanh cong
- cap nhat UI local thanh `NONE`
- doi nut `Huy yeu cau` thanh `Ket ban`

## 8. Danh sach loi moi ket ban da nhan

### 8.1. Request

`GET /api/v1/me/friend-requests`

### 8.2. Response toi thieu

```json
{
  "data": [
    {
      "friendshipId": "friendship-id",
      "senderId": "user-a",
      "senderUserName": "User A",
      "senderAvatar": "https://...",
      "receiverId": "current-user",
      "receiverUserName": "Current User",
      "receiverAvatar": "https://...",
      "reason": "Xin chao",
      "requestedAt": "2026-05-29T14:00:00Z"
    }
  ]
}
```

### 8.3. Hanh vi frontend
- dung danh sach nay de render man hinh `Loi moi ket ban`
- moi item nen co it nhat:
  - thong tin nguoi gui
  - ly do ket ban
  - thoi diem gui
  - nut `Chap nhan`
  - nut `Tu choi`

## 9. Danh sach loi moi ket ban da gui

### 9.1. Request

`GET /api/v1/me/friend-requests/sent`

### 9.2. Response toi thieu

```json
{
  "data": [
    {
      "friendshipId": "friendship-id",
      "senderId": "current-user",
      "senderUserName": "Current User",
      "senderAvatar": "https://...",
      "receiverId": "user-b",
      "receiverUserName": "User B",
      "receiverAvatar": "https://...",
      "reason": "Xin chao",
      "requestedAt": "2026-05-29T14:00:00Z"
    }
  ]
}
```

### 9.3. Hanh vi frontend
- dung danh sach nay de render man hinh `Da gui`
- moi item nen co it nhat:
  - thong tin nguoi nhan
  - ly do da gui
  - thoi diem gui
  - nut `Huy yeu cau`

## 10. Chap nhan loi moi ket ban

### 10.1. Dieu kien hien thi nut
- `canAcceptRequest = true`
- thong thuong tuong ung voi `relationStatus = INCOMING_REQUEST`

### 10.2. Request

`POST /api/v1/users/{targetId}/accept`

Trong route nay, `targetId` la id cua nguoi da gui loi moi cho toi.

### 10.3. Nghiep vu backend
- tim quan he giua hai ben
- chi receiver moi duoc chap nhan
- chi quan he `PENDING` moi duoc chap nhan
- cap nhat thanh `ACCEPTED`
- publish event `friend.accepted`

### 10.4. Response
- `200 OK`
- body rong

### 10.5. Hanh vi frontend sau khi thanh cong
- cap nhat UI local thanh `FRIEND`
- an nut `Chap nhan` va `Tu choi`
- hien thi lai cac nut `Nhan tin`, `Goi dien`, `Xoa khoi danh sach ban be` theo cac co `can*`

## 11. Su kien he thong khi tro thanh ban be

### 11.1. Truong hop da nhan tin truoc do
- Hai nguoi da co direct conversation va da nhan tin voi nhau truoc khi tro thanh ban be.
- Khi loi moi ket ban duoc chap nhan, service messaging van phai chen mot tin nhan su kien vao direct conversation hien co.
- Tin su kien nay phai la tin dau tien o phan timeline cuoc tro chuyen direct.
- Noi dung UI co the theo dang:
  - `Hai ban da tro thanh ban be`
  - hoac text tuong duong theo thiet ke UI

### 11.2. Truong hop chua nhan tin truoc do
- Neu hai ben chua tung nhan tin, sau khi tro thanh ban be service messaging tao direct conversation neu can.
- Ngay sau khi tro thanh ban be, cuoc tro chuyen phai co tin nhan su kien:
  - `Hai ban da tro thanh ban be`
  - hoac text tuong duong theo UI

### 11.3. Quy tac chung
- Tin su kien tro thanh ban be la system event cua cuoc tro chuyen direct.
- Tin nay phai xuat hien nhat quan cho ca hai ben.
- Frontend khong duoc tu hard-code local-only neu backend da co event that.

## 12. Tu choi loi moi ket ban

### 12.1. Dieu kien hien thi nut
- `canRejectRequest = true`
- thong thuong tuong ung voi `relationStatus = INCOMING_REQUEST`

### 12.2. Request

`POST /api/v1/users/{targetId}/reject`

Trong route nay, `targetId` la id cua nguoi da gui loi moi cho toi.

### 12.3. Nghiep vu backend
- chi receiver moi duoc tu choi
- chi quan he `PENDING` moi duoc tu choi
- xoa quan he friend request

### 12.4. Response
- `200 OK`
- body rong

### 12.5. Hanh vi frontend sau khi thanh cong
- cap nhat UI local thanh `NONE`
- an nut `Chap nhan` va `Tu choi`
- hien thi lai nut `Ket ban`

## 13. Xoa khoi danh sach ban be

### 13.1. Dieu kien hien thi nut
- `canUnfriend = true`
- thong thuong tuong ung voi `relationStatus = FRIEND`

### 13.2. Request

`DELETE /api/v1/users/{targetId}/unfriend`

### 13.3. Nghiep vu backend
- chi quan he `ACCEPTED` moi duoc xoa ban
- xoa relation friendship hien tai

### 13.4. Response
- `200 OK`
- body rong

### 13.5. Hanh vi frontend sau khi thanh cong
- cap nhat UI local thanh `NONE`
- an nut `Goi dien` va `Xoa khoi danh sach ban be`
- hien thi lai nut `Ket ban`

## 14. Route check de dong bo lai trang thai

### 14.1. Request

`GET /api/v1/users/{targetId}/check`

### 14.2. Response toi thieu

```json
{
  "data": {
    "currentUserId": "current-user",
    "targetUserId": "target-user",
    "relationStatus": "INCOMING_REQUEST",
    "isSelf": false,
    "canAddFriend": false,
    "canCancelRequest": false,
    "canAcceptRequest": true,
    "canRejectRequest": true,
    "canMessage": true,
    "canCall": false,
    "canUnfriend": false,
    "blocked": false
  }
}
```

### 14.3. Khi nao frontend nen goi route nay
- khi da co `targetId` va can dong bo lai trang thai hanh dong
- sau mot thao tac mutation neu frontend khong muon tu cap nhat local state
- khi mo lai profile cua mot nguoi da tim truoc do

## 15. Realtime danh sach cuoc tro chuyen

### 15.1. Yeu cau chung
- Danh sach cuoc tro chuyen phai duoc cap nhat realtime.
- Khi co tin nhan moi, system event moi, hoac thay doi quan he anh huong toi direct conversation, danh sach phai cap nhat ngay ma khong can reload man hinh.

### 15.2. Cac truong hop bat buoc cap nhat realtime
- Co tin nhan moi trong direct conversation.
- Cuoc tro chuyen moi duoc tao do hai ben bat dau nhan tin.
- Hai ben vua tro thanh ban be va system event `Hai ban da tro thanh ban be` duoc tao.
- Thu tu cuoc tro chuyen thay doi theo hoat dong moi nhat.
- Preview tin nhan cuoi cung thay doi.
- So luong chua doc thay doi.

### 15.3. Ky vong frontend
- Lang nghe su kien realtime tu backend.
- Cap nhat:
  - danh sach conversation
  - preview message
  - last activity
  - unread count
  - system event message trong direct conversation

## 16. State transition cho frontend

| Action | Trang thai truoc | Trang thai sau khi thanh cong |
| --- | --- | --- |
| Tim chinh minh | bat ky | `SELF` |
| Gui loi moi ket ban | `NONE` | `OUTGOING_REQUEST` |
| Huy loi moi | `OUTGOING_REQUEST` | `NONE` |
| Nhan loi moi tu doi phuong | `NONE` | `INCOMING_REQUEST` |
| Chap nhan loi moi | `INCOMING_REQUEST` | `FRIEND` |
| Tu choi loi moi | `INCOMING_REQUEST` | `NONE` |
| Xoa ban be | `FRIEND` | `NONE` |

## 17. Loi nghiep vu can thong nhat

Backend hien dang tra cac loi nghiep vu chu yeu bang `400` hoac `409` tuy loai exception, voi message text nhu:

- `User not found`
- `Sender not found`
- `Receiver not found`
- `A user cannot send friend request to themselves`
- `Cannot send friend request while block exists`
- `Friendship already active or pending`
- `Friend request not found`
- `Only requester can cancel this friend request`
- `Only the receiver can accept this friend request`
- `Only receiver can reject this friend request`
- `Only accepted friendship can be unfriended`

Frontend nen map cac loi nay thanh thong bao UI gon hon neu can.

## 18. Ket luan cho frontend

Frontend co hai cach dung:

1. Cach gon nhat cho modal tim kiem:
- goi `GET /api/v1/users/phone/{phone}/info`
- dung ngay `relationStatus` va cac co `can*` de render nut

2. Cach tach rieng:
- goi `GET /api/v1/users/phone/{phone}/info`
- sau do goi them `GET /api/v1/users/{targetId}/check` khi can dong bo trang thai hanh dong

Voi code backend hien tai, cach 1 da du cho flow tim kiem va ket ban.
