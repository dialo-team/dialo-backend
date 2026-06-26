# Flow direct conversation va realtime

Tai lieu nay la overview cho direct conversation va contract realtime toi thieu cua `services/messaging`.

Tai lieu lien quan:
- `docs/friend-request-flow.md`
- `docs/conversation-data-loading-flow.md`
- `docs/conversation-api-contract.md`
- `docs/message-realtime-flow.md`
- `docs/message-rendering-contract.md`
- `docs/attachment-upload-flow.md`

## 0. Pham vi tai lieu

Tai lieu nay chi giu cac quy tac tong quan:
- mo kha nang nhan tin dua tren `canMessage`
- tao hoac tai su dung direct conversation
- system event khi tro thanh ban be
- contract topic realtime
- danh sach testcase toi thieu

Phan chi tiet cho gui / nhan message realtime, typing, mark read, optimistic UI, dedupe message, va inbox refresh da duoc tach rieng tai:

- `docs/message-realtime-flow.md`

## Doc map

Doc bo nay dung toi muc nay la du, khong tach nho them nua.

- `docs/conversation-realtime-flow.md`
  - Doc overview tong quan direct conversation + realtime
- `docs/conversation-api-contract.md`
  - Doc khi can doi chieu request/response REST va payload STOMP
- `docs/conversation-data-loading-flow.md`
  - Doc khi can bootstrap inbox, mo thread, mark read, refetch list
- `docs/message-realtime-flow.md`
  - Doc khi can flow gui/nhan tin realtime, typing, inbox sync, dedupe
- `docs/message-rendering-contract.md`
  - Doc khi can quy tac render `SYSTEM` / `DEFAULT`, text, image, file, sticker, reply, poll
- `docs/message-error-retry-flow.md`
  - Doc khi can state `uploading/sending/failed/sent` va retry

Neu chi can onboard nhanh:
1. doc file nay
2. doc `docs/conversation-api-contract.md`
3. doc `docs/message-realtime-flow.md`

## 1. API backend dung trong flow

Quy tac ket noi hien tai cho frontend:
- REST API di qua gateway `http://localhost:9000`
- WebSocket/STOMP ket noi truc tiep `ws://localhost:8085/ws-messaging`
- Gateway validate `Authorization` va tu lay user id tu access token de gan `X-User-Id` khi forward request vao messaging

API toi thieu:
- `GET /api/v1/conversations`
- `GET /api/v1/conversations/{conversationId}`
- `POST /api/v1/conversations`
- `POST /api/v1/messages`
- `POST /api/v1/messages/direct`
- `PUT /api/v1/conversations/{conversationId}/read`
- `POST /api/v1/conversations/{conversationId}/typing`

Phan upload image/file that duoc mo ta rieng tai `docs/attachment-upload-flow.md`.

## 2. Nguon su that de mo kha nang nhan tin

Frontend khong tu quyet dinh duoc nhan tin hay khong.

Can doc tu `social-graph`:
- `relationStatus`
- `canMessage`

Quy tac:
- neu `canMessage = true` thi hien thi nut `Nhan tin`
- neu `canMessage = false` thi khong hien thi nut `Nhan tin`

Quy tac nay ap dung cho:
- `NONE`
- `OUTGOING_REQUEST`
- `INCOMING_REQUEST`
- `FRIEND`

## 3. Quy tac message model

### 3.1. System event
- system event duoc luu voi `type = SYSTEM`
- `system = true`
- frontend render theo kieu system message

### 3.2. Tin nhan thong thuong giua nguoi dung
- tin nhan thong thuong duoc luu voi `type = DEFAULT`
- frontend khong dung `type` de phan biet text / image / file / reply / poll / call
- frontend phai dua vao cac field trong message item de render:
  - `content`
  - `attachments`
  - `stickers`
  - `reference`
  - `poll`
  - `call`
  - `mentions`
  - `mentionEveryone`

Chi tiet luong gui / nhan item realtime nam trong `docs/message-realtime-flow.md`.
Chi tiet render message nam trong `docs/message-rendering-contract.md`.

## 4. Tao hoac mo direct conversation

### 4.1. Truong hop da co conversation
- Neu hai ben da tung nhan tin, backend phai tai su dung direct conversation hien co
- Khong tao direct conversation moi

### 4.2. Truong hop chua co conversation
- Neu hai ben chua co direct conversation, backend tao conversation moi
- Conversation moi phai xuat hien ngay trong danh sach inbox cua ca hai ben

### 4.3. API co the dung

#### Cach 1: Tao direct conversation truoc

`POST /api/v1/conversations`

```json
{
  "participantIds": ["current-user-id", "target-user-id"]
}
```

#### Cach 2: Gui tin nhan dau tien va backend tu tao conversation

`POST /api/v1/messages/direct`

```json
{
  "targetUserId": "target-user-id",
  "content": "Xin chao",
  "attachments": [],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null
}
```

Chi tiet xu ly realtime sau khi gui nam trong `docs/message-realtime-flow.md`.

## 5. System event khi tro thanh ban be

### 5.1. Truong hop da co direct conversation truoc do
- `messaging` nhan event RabbitMQ `friend.accepted`
- neu direct conversation da ton tai, backend chen them `SYSTEM` message vao direct conversation hien co
- khong tao direct conversation moi

### 5.2. Truong hop chua co direct conversation
- `messaging` nhan event RabbitMQ `friend.accepted`
- neu chua co direct conversation, backend tao direct conversation moi
- ngay sau do chen `SYSTEM` message vao conversation vua tao

### 5.3. Noi dung system event
- mac dinh: `Hai ban da tro thanh ban be`
- neu event payload co `systemMessage` thi uu tien noi dung do

## 6. Realtime contract

### 6.1. STOMP endpoint
- connect vao `ws://localhost:8085/ws-messaging`

### 6.2. Topic can subscribe

#### Inbox list cua user
- `/topic/inbox/{userId}`
- dung de dong bo `lastMessage`, `lastMessageAt`, `unreadCount`, va order sidebar

#### Thread cua conversation
- `/topic/conversations/{conversationId}`
- dung de nhan `MessageResponse` moi cua thread

#### Typing indicator
- `/topic/typing/{conversationId}`
- dung de hien indicator dang go

Chi tiet cach frontend xu ly 3 topic nay nam trong `docs/message-realtime-flow.md`.

## 7. Doc inbox va thread ban dau

Phan bootstrap va open thread duoc mo ta chi tiet tai `docs/conversation-data-loading-flow.md`.

Tom tat:
- `GET /api/v1/conversations` de doc inbox summary
- `GET /api/v1/conversations/{conversationId}` de doc chi tiet thread
- `PUT /api/v1/conversations/{conversationId}/read` de danh dau da doc

## 8. Ky vong UI va backend

### 8.1. Backend
- tra dung `canMessage` tu `social-graph`
- cho phep gui tin nhan dau tien voi `POST /api/v1/messages/direct`
- tai su dung direct conversation neu da ton tai
- luu tin nhan thong thuong voi `type = DEFAULT`
- tao system event khi `friend.accepted`
- phat realtime cho inbox, thread, typing

### 8.2. Frontend
- dung `canMessage` de mo / an nut `Nhan tin`
- subscribe inbox, thread, typing qua STOMP vao `ws://localhost:8085/ws-messaging`
- voi message `type = DEFAULT`, dung payload thuc te de render item
- neu la `image/file` that, frontend phai di qua flow upload trong `docs/attachment-upload-flow.md`
- ap dung dedupe theo `message.id` trong thread

## 9. Tinh huong can test

- Tim thay nguoi la, `canMessage = true`, gui duoc tin nhan dau tien bang `POST /api/v1/messages/direct`
- Tim thay nguoi la, `canMessage = false`, khong hien nut `Nhan tin`
- Gui mot item chi co anh, backend van chap nhan va luu `type = DEFAULT`
- Gui mot item co `content + attachments`, frontend render trong cung mot message item
- Gui mot item co `reference + content`, frontend render kieu reply
- Da co direct conversation, chap nhan ket ban, system event xuat hien trong chinh conversation cu
- Chua co direct conversation, chap nhan ket ban, backend tao conversation + system event
- Dang mo thread, nhan duoc realtime message moi
- Dang mo thread, nhan duoc realtime system event `Hai ban da tro thanh ban be`
- Dang mo thread, nhan duoc typing indicator
- Khong mo thread, inbox van cap nhat `lastMessage`, `unreadCount`, va thu tu conversation
