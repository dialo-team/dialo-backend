# Flow nhan tin realtime

Tai lieu nay chot flow nhan tin realtime cho direct va group conversation trong `dialo-desktop`, de frontend va backend co cung cach hieu khi gui tin, nhan tin, dong bo inbox, typing, mark read, poll, va tranh duplicate message trong thread.

Tai lieu lien quan:
- `docs/conversation-api-contract.md`
- `docs/conversation-data-loading-flow.md`
- `docs/conversation-realtime-flow.md`
- `docs/message-error-retry-flow.md`
- `docs/message-rendering-contract.md`
- `docs/attachment-upload-flow.md`
- `docs/friend-request-flow.md`

## 0. Pham vi tai lieu

Tai lieu nay dong cho flow realtime toi thieu:
- gui tin nhan text trong conversation da ton tai
- gui tin nhan dau tien cho direct chat
- gui tin nhan trong group chat
- gui sticker / attachment sau khi upload
- tao / ket thuc call item
- tao / vote / dong / xoa / them lua chon poll
- cap nhat reaction / pin trong thread
- thu hoi message trong thread
- forward message sang thread dich
- xoa message cho rieng current user
- xoa message cho toan bo participant
- sua text message da gui
- nhan message moi qua STOMP
- dong bo inbox khi co message moi
- hien typing indicator
- mark read khi dang mo thread
- dedupe message khi cung mot message di ve tu nhieu nguon

Tai lieu nay chua mo ta day du:
- pagination chinh thuc cua thread
- retry / dead-letter / resend contract ben backend
- offline queue
- read receipt chi tiet theo tung message

Phan state machine `uploading / sending / failed / sent` va flow retry da duoc tach rieng tai:

- `docs/message-error-retry-flow.md`

## 1. Nguon su that

REST API:
- di qua gateway `http://localhost:9000`

WebSocket/STOMP:
- ket noi truc tiep `ws://localhost:8085/ws-messaging`

Header frontend gui len REST:

```http
Authorization: Bearer <accessToken>
```

Header frontend gui luc STOMP CONNECT:

```http
Authorization: Bearer <accessToken>
```

Messaging se tu lay user id tu access token khi STOMP CONNECT.

Neu frontend cu van gui `X-User-Id` thi backend co the chap nhan de backward-compatible, nhung header nay khong duoc mau thuan voi access token.

Quy tac:
- REST dung de tao conversation, gui message, load thread, mark read
- STOMP dung de nhan thread event, inbox event, typing event
- frontend khong xem STOMP la noi nguon su that duy nhat
- backend REST van la contract chot cho hanh dong ghi du lieu

## 2. Topic realtime can subscribe

### 2.1. Inbox cua user

`/topic/inbox/{userId}`

Payload:
- uu tien la danh sach `ConversationSummaryResponse[]`
- neu payload chua on dinh thi frontend duoc phep bo qua body va refetch `GET /api/v1/conversations`

Muc dich:
- dong bo preview moi nhat
- dong bo `lastMessageAt`
- dong bo `unreadCount`
- dong bo thu tu conversation list

### 2.2. Thread cua conversation

`/topic/conversations/{conversationId}`

Payload:
- `MessageResponse`

Muc dich:
- append message moi vao thread dang mo
- hien system message moi
- cap nhat local thread nhanh hon truoc khi inbox refetch xong

### 2.3. Typing cua conversation

`/topic/typing/{conversationId}`

Payload toi thieu:

```json
{
  "conversationId": "conv-001",
  "userId": "user-002",
  "displayName": "Nguyen Van A",
  "typing": true
}
```

Muc dich:
- hien indicator dang go cho nguoi con lai

## 3. Flow bootstrap truoc khi nhan tin

1. Frontend vao man conversations
2. Goi `GET /api/v1/conversations`
3. User chon 1 conversation
4. Goi `GET /api/v1/conversations/{conversationId}`
5. Sau khi load thread thanh cong, goi `PUT /api/v1/conversations/{conversationId}/read`
6. Bat dau subscribe:
   - `/topic/inbox/{userId}`
   - `/topic/conversations/{conversationId}`
   - `/topic/typing/{conversationId}`

Luu y:
- Khong tron mock voi data that neu REST bootstrap thanh cong
- Neu user doi conversation, phai unsubscribe thread/topic cu roi subscribe topic moi

## 4. Gui tin nhan text trong conversation da ton tai

Frontend submit:

`POST /api/v1/messages`

Request:

```json
{
  "conversationId": "conv-001",
  "content": "Xin chao",
  "attachments": [],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null
}
```

Flow frontend:
1. Tao optimistic message local voi `sendStatus = "sending"`
2. Append optimistic message vao thread
3. Goi `POST /api/v1/messages`
4. Khi REST thanh cong:
   - map response thanh server message
   - thay optimistic message bang server message
   - dedupe theo `message.id`
5. Khi STOMP thread nhan cung message do:
   - append neu chua ton tai
   - bo qua neu `message.id` da co
6. Khi STOMP inbox den:
   - refetch `GET /api/v1/conversations`

Ky vong backend:
- luu message voi `type = DEFAULT`
- cap nhat `lastMessage`, `lastMessageAt`, `unreadCount`
- phat thread event cho conversation
- phat inbox event cho cac participant lien quan
- flow nay dung chung cho `DIRECT` va `GROUP`

## 5. Gui tin nhan dau tien cho direct chat

Co 2 cach hop le.

### 5.1. Cach 1: Tao hoac tai su dung conversation truoc

1. Frontend goi `POST /api/v1/conversations`
2. Backend tra direct conversation da ton tai hoac conversation moi
3. Frontend set `selectedConversationId`
4. Frontend goi `GET /api/v1/conversations/{conversationId}`
5. Frontend gui tin qua `POST /api/v1/messages`

### 5.2. Cach 2: Gui thang bang direct endpoint

Frontend goi:

`POST /api/v1/messages/direct`

Request:

```json
{
  "targetUserId": "user-002",
  "content": "Xin chao",
  "attachments": [],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null
}
```

Ky vong backend:
- neu da co direct conversation thi tai su dung
- neu chua co thi tao moi
- luu ngay tin nhan dau tien
- phat thread event cho conversation vua co
- phat inbox event cho ca hai ben

Ky vong frontend:
- dua conversation moi vao sidebar neu chua co
- mo thread do
- neu can, refetch `GET /api/v1/conversations/{conversationId}`

## 6. Gui attachment hoac sticker

### 6.1. Attachment

1. Frontend upload file theo `docs/attachment-upload-flow.md`
2. Nhan danh sach `attachments`
3. Goi `POST /api/v1/messages` voi `attachments`
4. Xu ly optimistic / REST success / STOMP event giong message text

Quy tac:
- `type` van la `DEFAULT`
- frontend render theo payload that
- chi tiet render nam trong `docs/message-rendering-contract.md`

### 6.2. Sticker

1. Frontend tao optimistic sticker message
2. Goi `POST /api/v1/messages` voi `stickers`
3. REST success -> thay optimistic item
4. STOMP thread -> dedupe theo `message.id`

## 7. Flow nhan message moi khi dang mo thread

Khi frontend dang subscribe `/topic/conversations/{conversationId}`:

1. Nhan `MessageResponse`
2. Map payload thanh view model cua thread
3. Kiem tra `message.id`
4. Neu chua ton tai trong thread local:
   - append vao thread
   - sort lai theo `position`, neu can thi fallback `createdAt`
5. Neu da ton tai:
   - bo qua
   - hoac thay item cu neu day la ban server day du hon
6. Neu `senderId != currentUserId` va user dang mo thread:
   - goi `PUT /api/v1/conversations/{conversationId}/read`
   - clear unread local cua conversation do
7. Doi event inbox hoac chu dong refetch list de dong bo sidebar

## 7.1. Flow reaction va pin update

Frontend goi:
- `POST /api/v1/messages/{messageId}/reactions`
- `DELETE /api/v1/messages/{messageId}/reactions/{emoji}`
- `PUT /api/v1/messages/{messageId}/pin`
- `DELETE /api/v1/messages/{messageId}/pin`
- `PUT /api/v1/messages/{messageId}/revoke`

Ky vong backend:
- tra ve `MessageResponse` da cap nhat
- phat lai `MessageResponse` moi len `/topic/conversations/{conversationId}`
- khong phat inbox event chi vi reaction/pin thay doi

Ky vong frontend:
1. cap nhat bubble hien tai theo `message.id`
2. khong append bubble moi
3. render lai `reactions` va `pinned`
4. neu STOMP ve sau REST thi van upsert theo `message.id`

## 7.2. Flow revoke update

Frontend goi:
- `PUT /api/v1/messages/{messageId}/revoke`

Ky vong backend:
- tra ve `MessageResponse` da cap nhat voi:
  - `deleted = true`
  - `deletedTimeStamp != null`
  - `content = "Tin nhan da duoc thu hoi"`
- phat lai `MessageResponse` moi len `/topic/conversations/{conversationId}`
- phat inbox event de sidebar doi preview neu day la last message

Ky vong frontend:
1. update bubble hien tai theo `message.id`
2. khong append bubble moi
3. render placeholder thu hoi thay cho payload cu
4. neu STOMP ve sau REST thi van upsert theo `message.id`

## 7.3. Flow forward

Frontend goi:
- `POST /api/v1/messages/{messageId}/forward`

voi mot trong hai dich:
- `conversationId`
- `targetUserId`

Ky vong backend:
- tao message moi o conversation dich
- neu la `targetUserId` thi backend tu tao/reuse direct conversation dich
- tra `MessageResponse` moi o dich den
- phat thread event o conversation dich
- phat inbox event cho participant cua conversation dich

Ky vong frontend:
1. khong update bubble nguon
2. append/upsert message moi trong thread dich neu thread dich dang mo
3. neu thread dich chua mo thi doi inbox event roi refetch list

## 7.4. Flow delete for me

Frontend goi:
- `DELETE /api/v1/messages/{messageId}`

Ky vong backend:
- danh dau hidden theo current user
- tra ack:
  - `messageId`
  - `conversationId`
  - `deletedForMe = true`
- khong phat thread event chung
- phat inbox event cho current user neu can cap nhat sidebar

Ky vong frontend:
1. bo bubble local ngay sau REST success
2. khong cho STOMP thread event de xac nhan
3. khi refetch thread sau nay, message da xoa cho minh phai bien mat

## 7.4.1. Flow delete for everyone

Frontend goi:
- `DELETE /api/v1/messages/{messageId}/everyone`

Ky vong backend:
- chi sender moi duoc xoa cho moi nguoi
- tra `MessageResponse` da cap nhat voi:
  - `content = "Tin nhan da bi xoa"`
  - `deleted = true`
  - `deletedForEveryone = true`
- phat lai `MessageResponse` moi len `/topic/conversations/{conversationId}`
- phat inbox event de sidebar doi preview neu day la last message

Ky vong frontend:
1. update bubble hien tai theo `message.id`
2. khong append bubble moi
3. render placeholder xoa cho moi nguoi khac voi revoke
4. neu STOMP ve sau REST thi van upsert theo `message.id`

## 7.5. Flow edit

Frontend goi:
- `PATCH /api/v1/messages/{messageId}`

Ky vong backend:
- cap nhat `content`
- cap nhat `editedTimeStamp`
- giu nguyen `message.id`
- phat thread event va inbox event

Ky vong frontend:
1. update bubble cu theo `message.id`
2. khong append bubble moi
3. co the hien dau hieu `da chinh sua` dua tren `editedTimeStamp`

## 8. Flow nhan message moi khi khong mo thread

1. Frontend van subscribe `/topic/inbox/{userId}`
2. Khi co event inbox:
   - refetch `GET /api/v1/conversations`
3. Sidebar cap nhat:
   - preview
   - `lastMessageAt`
   - `unreadCount`
   - order moi nhat len dau
4. Thread khong can fetch lai cho toi khi user mo conversation do

## 8.1. Flow poll realtime

Frontend goi:
- `POST /api/v1/conversations/{conversationId}/polls` de tao poll
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes` de vote
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/options` de them lua chon
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/close` de dong poll
- `DELETE /api/v1/conversations/{conversationId}/polls/{messageId}` de xoa poll

Ky vong backend:
- poll duoc xem la mot `MessageResponse` co field `poll`
- moi thay doi poll deu phat lai `MessageResponse` moi len `/topic/conversations/{conversationId}`
- poll create/update co inbox event neu anh huong `lastMessage`

Ky vong frontend:
1. upsert bubble poll theo `message.id`
2. khong append bubble moi khi vote/close/add option
3. `selectedAnswerIds` nen uu tien tu REST response requester-specific
4. thread event STOMP chi dung de cap nhat aggregate count/finalized state nhanh

## 8.2. Flow call realtime

Frontend goi:
- `POST /api/v1/conversations/{conversationId}/calls`
- `POST /api/v1/conversations/{conversationId}/calls/{messageId}/end`

Ky vong backend:
- tao/doi state call tren cung `MessageResponse`
- call item moi phat thread event + inbox event
- end call phat thread update theo cung `message.id`

Ky vong frontend:
1. render call item theo `call.type/status/users/startedTime/endedTime`
2. end call la upsert bubble cu theo `message.id`
3. day khong phai signaling/WebRTC stream contract

## 9. Typing realtime

Khi user go phim trong direct thread:

1. Frontend goi `POST /api/v1/conversations/{conversationId}/typing`
2. Backend phat event len `/topic/typing/{conversationId}`
3. Frontend ben kia nhan event:
   - neu `userId` khac current user
   - va `typing = true`
   - thi hien indicator tam thoi
4. Frontend tu clear indicator sau khoang 3 giay neu khong co event moi

Quy tac:
- Typing khong tao message
- Typing khong cap nhat inbox
- Typing khong di qua topic thread message

## 10. Mark read realtime

Truong hop user dang mo thread:

1. Thread load xong
2. Frontend goi `PUT /api/v1/conversations/{conversationId}/read`
3. Backend dua unread cua user do ve `0`
4. Backend phat thread update tren last-read message voi `readBy` moi
5. Backend phat inbox event
6. Moi view dang subscribe inbox refetch lai list

Truong hop dang mo thread va nhan message moi:

1. STOMP thread nhan message moi tu nguoi kia
2. Frontend append vao thread
3. Frontend goi `PUT /read`
4. Backend phat inbox event moi

## 11. Quy tac dedupe message

Day la quy tac bat buoc de tranh duplicate key va duplicate bubble trong UI.

Co 3 nguon co the dua cung mot message vao thread:
- optimistic local append
- REST response sau khi gui thanh cong
- STOMP `/topic/conversations/{conversationId}`

Quy tac frontend:
- message trong cung mot conversation phai unique theo `message.id`
- moi lan append hoac replace deu phai normalize lai mang message
- neu STOMP da dua server message vao thread truoc:
  - khi REST response ve, frontend thay optimistic item va collapse duplicate
- neu REST da thay optimistic item truoc:
  - khi STOMP ve sau, frontend bo qua vi `message.id` da ton tai

Khong duoc:
- append mu quang moi payload vao thread ma khong check `message.id`
- dung `index` lam React key cho bubble message

## 12. Truong hop loi toi thieu

### 12.1. REST gui message loi
- optimistic item doi `sendStatus = "failed"`
- cho phep retry
- khong tu append server message gia

Chi tiet retry nam trong `docs/message-error-retry-flow.md`.

### 12.2. STOMP mat ket noi
- UI van cho phep gui message qua REST neu session con hop le
- frontend nen reconnect STOMP
- sau reconnect, frontend nen giu inbox sync bang `GET /api/v1/conversations`
- neu user dang o thread, frontend co the refetch `GET /api/v1/conversations/{conversationId}`

### 12.3. REST thread detail loi
- giu state hien tai neu dang co message local
- hien trang thai khong dong bo duoc du lieu moi nhat

## 13. Checklist frontend

- Subscribe `/topic/inbox/{userId}` sau khi co session
- Mo thread thi subscribe `/topic/conversations/{conversationId}`
- Mo direct thread thi subscribe them `/topic/typing/{conversationId}`
- Mo group thread thi typing topic van dung chung `/topic/typing/{conversationId}`
- Gui text bang `POST /api/v1/messages`
- Gui first message direct bang `POST /api/v1/messages/direct` hoac flow tao conversation truoc
- Upload attachment truoc khi gui attachment message
- Sau khi load thread thanh cong thi goi `PUT /read`
- Khi nhan thread event thi dedupe theo `message.id`
- Khi nhan thread event cua reaction/pin thi upsert lai message theo `message.id`
- Khi nhan thread event cua revoke thi thay noi dung bubble cu theo `message.id`
- Khi nhan inbox event thi refetch `GET /api/v1/conversations`
- Khi nhan typing event thi chi hien indicator tam thoi

## 14. Checklist backend

- `POST /api/v1/messages` luu message va phat thread + inbox event
- `POST /api/v1/messages/direct` tai su dung hoac tao direct conversation, roi phat thread + inbox event
- `POST /api/v1/conversations/{conversationId}/polls` tao poll message va phat thread + inbox event
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes` tra `MessageResponse` poll update va phat thread update
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/options` tra `MessageResponse` poll update va phat thread update
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/close` tra `MessageResponse` poll update va phat thread update
- `DELETE /api/v1/conversations/{conversationId}/polls/{messageId}` tra `MessageResponse` revoke poll va phat thread + inbox update
- `POST/DELETE /api/v1/messages/{messageId}/reactions...` tra `MessageResponse` va phat thread update
- `PUT/DELETE /api/v1/messages/{messageId}/pin` tra `MessageResponse` va phat thread update
- `PUT /api/v1/messages/{messageId}/revoke` tra `MessageResponse` va phat thread + inbox update
- `POST /api/v1/messages/{messageId}/forward` tra `MessageResponse` moi o dich den va phat thread + inbox update
- `DELETE /api/v1/messages/{messageId}` tra ack `delete for me` va cap nhat inbox cho current user
- `DELETE /api/v1/messages/{messageId}/everyone` tra `MessageResponse` delete-for-everyone va phat thread + inbox update
- `PATCH /api/v1/messages/{messageId}` tra `MessageResponse` edit va phat thread + inbox update
- `POST /api/v1/conversations/{conversationId}/typing` phat typing event
- `PUT /api/v1/conversations/{conversationId}/read` cap nhat unread va phat inbox event
- Thread event phai co `message.id` on dinh
- Thread event co the duoc dung de dong bo `readBy` khi user khac vua mark read
- Inbox event phai du de frontend dong bo lai sidebar, hoac frontend co the refetch list
