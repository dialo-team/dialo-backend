# Contract API conversation

Tai lieu nay gom contract REST va STOMP toi thieu cho conversation/message trong `dialo-desktop`.

Muc tieu:
- frontend co 1 noi duy nhat de doi chieu request/response shape
- backend co 1 hop dong toi thieu de giu on dinh khi frontend tich hop
- cac tai lieu flow khong can lap lai payload qua nhieu

Tai lieu lien quan:
- `docs/conversation-data-loading-flow.md`
- `docs/conversation-realtime-flow.md`
- `docs/message-realtime-flow.md`
- `docs/message-rendering-contract.md`
- `docs/message-error-retry-flow.md`
- `docs/attachment-upload-flow.md`

## 0. Pham vi tai lieu

Tai lieu nay dong cho contract toi thieu cua:
- `GET /api/v1/conversations`
- `GET /api/v1/conversations/{conversationId}`
- `POST /api/v1/conversations`
- `POST /api/v1/conversations/groups`
- `POST /api/v1/conversations/{conversationId}/members`
- `DELETE /api/v1/conversations/{conversationId}/members/{memberUserId}`
- `PUT /api/v1/conversations/{conversationId}/members/{memberUserId}/role`
- `PUT /api/v1/conversations/{conversationId}/owner`
- `POST /api/v1/conversations/{conversationId}/leave`
- `DELETE /api/v1/conversations/{conversationId}`
- `POST /api/v1/conversations/{conversationId}/polls`
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes`
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/options`
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/close`
- `DELETE /api/v1/conversations/{conversationId}/polls/{messageId}`
- `GET /api/v1/conversations/{conversationId}/polls/{messageId}/answers/{answerId}`
- `POST /api/v1/conversations/{conversationId}/calls`
- `POST /api/v1/conversations/{conversationId}/calls/{messageId}/end`
- `POST /api/v1/messages`
- `POST /api/v1/messages/direct`
- `POST /api/v1/messages/{messageId}/reactions`
- `DELETE /api/v1/messages/{messageId}/reactions/{emoji}`
- `PUT /api/v1/messages/{messageId}/pin`
- `DELETE /api/v1/messages/{messageId}/pin`
- `PUT /api/v1/messages/{messageId}/revoke`
- `POST /api/v1/messages/{messageId}/forward`
- `DELETE /api/v1/messages/{messageId}`
- `DELETE /api/v1/messages/{messageId}/everyone`
- `PATCH /api/v1/messages/{messageId}`
- `PUT /api/v1/conversations/{conversationId}/read`
- `POST /api/v1/conversations/{conversationId}/typing`
- `/topic/inbox/{userId}`
- `/topic/conversations/{conversationId}`
- `/topic/typing/{conversationId}`

Tai lieu nay chua dong cho:
- contract loi chi tiet
- pagination chinh thuc
- filter/sort/search query param chinh thuc
- upload attachment chi tiet
- upload attachment chi tiet

## 1. Ket noi va header

### 1.1. REST

REST di qua gateway:

`http://localhost:9000`

Header frontend toi thieu:

```http
Authorization: Bearer <accessToken>
```

Gateway se tu lay user id tu token va forward vao messaging bang:

```http
X-User-Id: <current-user-id>
```

### 1.2. STOMP/WebSocket

Ket noi truc tiep:

`ws://localhost:8085/ws-messaging`

Header STOMP CONNECT toi thieu:

```http
Authorization: Bearer <accessToken>
```

Messaging se tu lay user id tu access token trong luc STOMP CONNECT.

Neu frontend cu van gui:

```http
X-User-Id: <current-user-id>
```

thi backend co the chap nhan de backward-compatible, nhung neu header nay khong khop voi subject trong access token thi ket noi se bi tu choi.

## 2. Kieu du lieu cot loi

## 2.1. Conversation summary toi thieu

```json
{
  "conversationId": "conv-001",
  "type": "DIRECT",
  "counterpartId": "user-002",
  "counterpartName": "Nguyen Van A",
  "counterpartAvatarUrl": "https://...",
  "groupName": null,
  "groupAvatarUrl": null,
  "lastMessageId": "msg-001",
  "lastMessage": "Xin chao",
  "lastMessagePreview": "Xin chao",
  "lastMessageSenderId": "user-002",
  "lastMessageType": "DEFAULT",
  "lastMessageAt": "2026-05-30T09:10:00Z",
  "lastMessageSystem": false,
  "unreadCount": 3
}
```

Quy tac:
- `conversationId` la field uu tien; `id` co the duoc chap nhan nhu fallback
- `type` co the la `DIRECT` hoac `GROUP`
- frontend uu tien dung `lastMessage` hoac `lastMessagePreview` cho sidebar preview
- voi `GROUP`, backend nen tra them neu co:
  - `ownerId`
  - `activeMemberCount`
  - `myRole`
  - `blocked`

## 2.2. Message response toi thieu

```json
{
  "id": "msg-001",
  "conversationId": "conv-001",
  "senderId": "user-002",
  "senderName": "Nguyen Van A",
  "type": "DEFAULT",
  "content": "Xin chao",
  "attachments": [],
  "stickers": [],
  "mentions": [],
  "mentionEveryone": false,
  "reference": null,
  "poll": null,
  "call": null,
  "reactions": [],
  "readBy": [],
  "pinned": false,
  "system": false,
  "position": 12,
  "createdAt": "2026-05-30T09:10:00Z",
  "editedTimeStamp": null,
  "deletedTimeStamp": null,
  "deleted": false,
  "deletedForEveryone": false
}
```

Quy tac:
- `id` phai on dinh va unique trong conversation
- `conversationId` bat buoc co
- `type` khong du de frontend render, frontend phai doc payload that
- `system = true` hoac `type = SYSTEM` thi render system message
- `reactions` la summary theo emoji cho current payload
- `readBy` la danh sach member da doc den message nay, dua tren `lastReadPosition`
- `pinned` cho biet message dang duoc ghim hay khong

## 2.3. Attachment

```json
{
  "id": "att-001",
  "fileName": "photo.png",
  "contentType": "image/png",
  "size": 123456,
  "url": "https://..."
}
```

Field co the co them:
- `width`
- `height`
- `title`
- `description`
- `durationSecs`

## 2.4. Sticker

```json
{
  "id": "sticker-001",
  "url": "https://..."
}
```

## 2.5. Reference

```json
{
  "messageId": "msg-000",
  "conversationId": "conv-001",
  "exist": true
}
```

## 2.6. Poll

```json
{
  "question": "An trua gi?",
  "answers": [
    { "id": 1, "content": "Com" },
    { "id": 2, "content": "Pho" }
  ],
  "expiry": "2026-05-30T12:00:00Z",
  "allowMultiSelect": false,
  "results": {
    "isFinalized": false,
    "answerCounts": [
      { "answerId": 1, "count": 3 },
      { "answerId": 2, "count": 5 }
    ],
    "selectedAnswerIds": [2]
  }
}
```

Quy tac:
- `selectedAnswerIds` chi co y nghia day du tren response REST requester-specific
- thread event STOMP co the tra `selectedAnswerIds = []` hoac bo trong vi day la topic dung chung

## 2.7. Call

```json
{
  "type": "VIDEO",
  "status": "ONGOING",
  "users": [
    { "userId": "user-001", "nick": "User 1", "avatarUrl": "https://..." },
    { "userId": "user-002", "nick": "User 2", "avatarUrl": "https://..." }
  ],
  "startedTime": "2026-05-30T10:00:00Z",
  "endedTime": null
}
```

## 3. `GET /api/v1/conversations`

Muc dich:
- bootstrap sidebar conversation list

### 3.1. Request

```http
GET /api/v1/conversations
Authorization: Bearer <accessToken>
```

### 3.2. Response hop le

Frontend nen chap nhan mot trong cac shape:

#### Shape A: array truc tiep

```json
[
  {
    "conversationId": "conv-001",
    "type": "DIRECT",
    "counterpartName": "Nguyen Van A",
    "lastMessage": "Xin chao",
    "lastMessageAt": "2026-05-30T09:10:00Z",
    "unreadCount": 3
  }
]
```

#### Shape B: envelope `items`

```json
{
  "items": [
    {
      "conversationId": "conv-001",
      "type": "DIRECT"
    }
  ]
}
```

#### Shape C: envelope `conversations`

```json
{
  "conversations": [
    {
      "conversationId": "conv-001",
      "type": "DIRECT"
    }
  ]
}
```

#### Shape D: envelope `content` hoac `data`

```json
{
  "data": [
    {
      "conversationId": "conv-001",
      "type": "DIRECT"
    }
  ]
}
```

### 3.3. Quy tac frontend parse

Frontend uu tien:
- `conversationId`
- fallback `id`

Frontend co the chap nhan:
- `type`
- fallback `conversationType`

Frontend co the chap nhan preview tu:
- `lastMessage`
- `lastMessagePreview`
- `lastMessageContent`
- `lastMessageData.content`

## 4. `GET /api/v1/conversations/{conversationId}`

Muc dich:
- load thread cua 1 conversation

### 4.1. Request

```http
GET /api/v1/conversations/conv-001
Authorization: Bearer <accessToken>
```

### 4.2. Response toi thieu

```json
{
  "conversationId": "conv-001",
  "counterpartId": "user-002",
  "counterpartName": "Nguyen Van A",
  "counterpartAvatarUrl": "https://...",
  "unreadCount": 3,
  "messages": [
    {
      "id": "msg-001",
      "conversationId": "conv-001",
      "senderId": "user-002",
      "senderName": "Nguyen Van A",
      "type": "DEFAULT",
      "content": "Xin chao",
      "attachments": [],
      "stickers": [],
      "mentions": [],
      "mentionEveryone": false,
      "reference": null,
      "poll": null,
      "call": null,
      "system": false,
      "position": 1,
      "createdAt": "2026-05-30T09:10:00Z"
    }
  ]
}
```

Quy tac:
- `messages` la nguon render thread
- direct thread nen co `counterpartName` va `counterpartAvatarUrl`
- frontend phai mark read sau khi load thread thanh cong
- group detail nen co them neu co:
  - `groupName`
  - `groupAvatarUrl`
  - `ownerId`
  - `activeMemberCount`
  - `myRole`
  - `blocked`
  - `blockedMessage`
  - `members`

Neu requester dang bi block trong group:
- backend van co the tra:
  - `conversationId`
  - `type = GROUP`
  - `groupName`
  - `groupAvatarUrl`
  - `blocked = true`
  - `blockedMessage`
- backend khong nen tra:
  - `messages`
  - `members`
  - bulletin/archive/group detail noi bo

## 5. `POST /api/v1/conversations`

Muc dich:
- tao hoac tai su dung direct conversation

### 5.1. Request

```json
{
  "participantIds": ["current-user-id", "target-user-id"]
}
```

### 5.2. Response toi thieu

```json
{
  "conversationId": "conv-001",
  "type": "DIRECT",
  "counterpartId": "user-002",
  "counterpartName": "Nguyen Van A",
  "counterpartAvatarUrl": "https://..."
}
```

Quy tac:
- neu direct conversation da ton tai thi tra conversation cu
- neu chua co thi tao moi

## 5.1. `POST /api/v1/conversations/groups`

Muc dich:
- tao group conversation

### Request

```json
{
  "name": "Nhom hoc tap",
  "participantIds": ["user-002", "user-003"],
  "avatarUrl": "https://...",
  "description": "Mo ta nhom"
}
```

### Response toi thieu

```json
{
  "conversationId": "group-001",
  "type": "GROUP",
  "groupName": "Nhom hoc tap",
  "groupAvatarUrl": "https://..."
}
```

Quy tac:
- group phai co it nhat 3 nguoi tong cong, tinh ca requester
- requester se tu dong la member cua nhom
- requester duoc tao voi `role = OWNER`
- cac participant con lai duoc tao voi `role = MEMBER`
- backend phat inbox event cho toan bo member sau khi tao nhom

## 5.2. `POST /api/v1/conversations/{conversationId}/members`

Muc dich:
- them member vao group

### Request

```json
{
  "participantIds": ["user-004", "user-005"]
}
```

### Response toi thieu

Tra ve `ConversationSummaryResponse` cua group.

Quy tac:
- chi `OWNER` hoac `ADMIN` moi them duoc member
- endpoint nay chi hop le voi `GROUP`
- member da ton tai trong group se duoc bo qua
- member moi se duoc tao voi `role = MEMBER`
- backend phat inbox event cho toan bo member, bao gom user moi vua duoc them

## 5.3. `DELETE /api/v1/conversations/{conversationId}/members/{memberUserId}`

Muc dich:
- xoa member khoi group

### Response toi thieu

```json
{
  "conversationId": "group-001",
  "removedUserId": "user-004",
  "removed": true
}
```

Quy tac:
- chi `OWNER/ADMIN` moi duoc xoa member
- `OWNER` co the xoa `ADMIN/MEMBER`
- `ADMIN` chi duoc xoa `MEMBER`
- khong dung endpoint nay de tu roi nhom
- backend soft-remove membership bang `leftAt`
- backend phat inbox event cho member con lai; user bi xoa se khong con thay group sau khi refetch

## 5.4. `PUT /api/v1/conversations/{conversationId}/members/{memberUserId}/role`

Muc dich:
- doi role cua member trong group

### Request

```json
{
  "role": "ADMIN"
}
```

### Response toi thieu

```json
{
  "conversationId": "group-001",
  "userId": "user-004",
  "role": "ADMIN"
}
```

Quy tac:
- chi `OWNER` moi duoc doi role
- flow hien tai chi ho tro doi giua `ADMIN` va `MEMBER`
- backend phat inbox event cho member de dong bo thong tin nhom neu can

## 5.4.1. `PUT /api/v1/conversations/{conversationId}/owner`

Muc dich:
- chuyen owner cua group

### Request

```json
{
  "newOwnerUserId": "user-004"
}
```

### Response toi thieu

```json
{
  "conversationId": "group-001",
  "previousOwnerUserId": "user-001",
  "newOwnerUserId": "user-004",
  "transferred": true
}
```

Quy tac:
- chi `OWNER` hien tai moi duoc chuyen owner
- owner moi phai la active member cua group
- owner cu xuong `ADMIN`
- owner moi len `OWNER`
- `conversation.ownerId` duoc cap nhat theo owner moi
- backend phat inbox event cho member de dong bo thong tin nhom neu can

## 5.5. `POST /api/v1/conversations/{conversationId}/leave`

Muc dich:
- current user roi khoi group

### Response toi thieu

```json
{
  "conversationId": "group-001",
  "userId": "user-004",
  "left": true
}
```

Quy tac:
- chi dung cho `GROUP`
- `OWNER` phai transfer owner truoc roi moi duoc leave
- membership duoc soft-remove bang `leftAt`
- sau khi thanh cong, current user se khong con thay group sau khi refetch

## 5.6. `DELETE /api/v1/conversations/{conversationId}`

Muc dich:
- giai tan group

### Response toi thieu

```json
{
  "conversationId": "group-001",
  "dissolved": true
}
```

Quy tac:
- chi `OWNER` moi duoc giai tan nhom
- chi dung cho `GROUP`
- backend danh dau `Conversation.status = DELETE`
- sau khi giai tan, group khong con xuat hien trong inbox refetch cua member

## 5.7. Poll management

Luu y:
- poll la mot `Message` co payload `poll`
- group chat va direct chat deu dung chung message/thread contract nay

## 5.7.1. Notes

Luu y:
- notes la du lieu that, khong con la mock/no-op
- backend can cap contract rieng cho list/detail/create/update/delete note item
- note item toi thieu nen co:
  - `noteId`
  - `conversationId`
  - `authorUserId`
  - `authorName`
  - `authorAvatarUrl`
  - `content`
  - `contentPreview`
  - `createdAt`
  - `updatedAt`
  - `canEdit`
  - `canDelete`

### `POST /api/v1/conversations/{conversationId}/polls`

```json
{
  "content": "Binh chon bua toi",
  "question": "An gi toi nay?",
  "options": ["Pho", "Bun bo"],
  "allowMultiSelect": false,
  "expiry": "2026-05-30T12:00:00Z"
}
```

### `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes`

```json
{
  "answerIds": [1]
}
```

Response:
- tra `MessageResponse` da cap nhat `poll.results`

Quy tac:
- poll single-select thi chi duoc chon 1 dap an
- vote lai se thay the vote cu cua current user

### `POST /api/v1/conversations/{conversationId}/polls/{messageId}/options`

```json
{
  "content": "Com tam"
}
```

Response:
- tra `MessageResponse` da cap nhat

Quy tac:
- chi nguoi tao poll moi duoc them lua chon
- khong them option neu poll da dong/het han

### `POST /api/v1/conversations/{conversationId}/polls/{messageId}/close`

Response:
- tra `MessageResponse` da cap nhat voi `poll.results.isFinalized = true`

Quy tac:
- chi nguoi tao poll moi duoc dong poll

### `DELETE /api/v1/conversations/{conversationId}/polls/{messageId}`

Response:
- tra `MessageResponse` da cap nhat

Quy tac:
- chi nguoi tao poll moi duoc xoa poll
- flow nay thu hoi poll message va xoa vote da luu

### `GET /api/v1/conversations/{conversationId}/polls/{messageId}/answers/{answerId}`

Response toi thieu:

```json
{
  "conversationId": "group-001",
  "messageId": "msg-poll-001",
  "answerId": 1,
  "total": 2,
  "voters": [
    {
      "userId": "user-002",
      "nick": "Nguyen Van A",
      "avatarUrl": "https://..."
    }
  ]
}
```

## 5.8. Call management

### `POST /api/v1/conversations/{conversationId}/calls`

Request:

```json
{
  "type": "VIDEO",
  "userIds": ["user-001", "user-002"]
}
```

Response:
- tra `MessageResponse` moi co field `call`

Quy tac:
- dung cho direct va group
- neu `userIds` rong, backend se suy ra active member cua conversation
- backend tao call item voi:
  - `status = ONGOING`
  - `startedTime`
- backend phat thread event + inbox event

### `POST /api/v1/conversations/{conversationId}/calls/{messageId}/end`

Response:
- tra `MessageResponse` da cap nhat voi:
  - `call.status = ENDED`
  - `call.endedTime != null`

Quy tac:
- chi end tren message call co san trong conversation
- backend phat thread event update theo cung `message.id`

## 6. `POST /api/v1/messages`

Muc dich:
- gui message trong conversation da ton tai

### 6.1. Request

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

### 6.2. Response toi thieu

Tra ve `MessageResponse`.

Vi du:

```json
{
  "id": "msg-001",
  "conversationId": "conv-001",
  "senderId": "current-user-id",
  "type": "DEFAULT",
  "content": "Xin chao",
  "attachments": [],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null,
  "system": false,
  "createdAt": "2026-05-30T09:10:00Z"
}
```

Quy tac:
- message thong thuong luu voi `type = DEFAULT`
- response thanh cong phai dung cung `message.id` voi thread event STOMP sau do
- API nay dung duoc cho ca `DIRECT` va `GROUP`, mien current user la active member cua conversation

## 7. `POST /api/v1/messages/direct`

Muc dich:
- gui tin nhan dau tien cho direct chat va de backend tu tai su dung/tao conversation

### 7.1. Request

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

### 7.2. Response toi thieu

Tra ve `MessageResponse`.

Quy tac:
- neu da co direct conversation thi tai su dung
- neu chua co thi tao moi roi luu message
- response phai chua `conversationId`

## 8. `PUT /api/v1/conversations/{conversationId}/read`

Muc dich:
- mark read cho conversation

### 8.1. Request

```http
PUT /api/v1/conversations/conv-001/read
Authorization: Bearer <accessToken>
```

### 8.2. Response

Frontend khong can body bat buoc.

Quy tac:
- unread cua current user trong conversation do ve `0`
- backend nen phat inbox event sau khi mark read
- backend phat them thread update tren last-read message de dong bo `readBy`

## 8.1. `POST /api/v1/messages/{messageId}/reactions`

Muc dich:
- them reaction cho message

### Request

```json
{
  "emoji": 128077
}
```

### Response

Tra ve `MessageResponse` da cap nhat.

Quy tac:
- user phai la member cua conversation chua message
- cung `user + message + emoji` la idempotent, goi lap lai khong tao duplicate
- backend phat thread event voi cung `message.id`
- response nen tra `reactions[].emoji` la display-ready emoji string de frontend render truc tiep

## 8.2. `DELETE /api/v1/messages/{messageId}/reactions/{emoji}`

Muc dich:
- bo reaction cua current user khoi message

### Response

Tra ve `MessageResponse` da cap nhat.

Quy tac:
- neu reaction chua ton tai thi xem nhu no-op hop le
- backend phat thread event voi cung `message.id`

## 8.3. `PUT /api/v1/messages/{messageId}/pin`

Muc dich:
- ghim message

### Response

Tra ve `MessageResponse` da cap nhat voi `pinned = true`.

## 8.4. `DELETE /api/v1/messages/{messageId}/pin`

Muc dich:
- bo ghim message

### Response

Tra ve `MessageResponse` da cap nhat voi `pinned = false`.

## 8.5. `PUT /api/v1/messages/{messageId}/revoke`

Muc dich:
- thu hoi message cho toan conversation

### Response

Tra ve `MessageResponse` da cap nhat.

Quy tac:
- chi nguoi gui moi duoc thu hoi
- khong duoc thu hoi `SYSTEM` message
- neu message da thu hoi roi thi request la idempotent
- backend tra payload da duoc chuan hoa:
  - `content = "Tin nhan da duoc thu hoi"`
  - `deleted = true`
  - `deletedTimeStamp != null`
- `attachments/stickers/reactions` rong
- backend phat thread event va inbox event voi cung `message.id`

## 8.6. `POST /api/v1/messages/{messageId}/forward`

Muc dich:
- chuyen tiep message sang conversation khac hoac direct target khac

### Request

Chon mot trong hai shape:

```json
{
  "conversationId": "conv-002"
}
```

hoac:

```json
{
  "targetUserId": "user-003"
}
```

### Response

Tra ve `MessageResponse` moi o dich den.

Quy tac:
- chi duoc chon mot dich `conversationId` hoac `targetUserId`
- khong cho forward `SYSTEM` message
- khong cho forward message da thu hoi
- forward hien tai clone payload sang message moi:
  - `content`
  - `attachments`
  - `stickers`
  - `poll`
  - `call`
- khong mang theo `reference`, `mentions`, `reactions`, `pinned`

## 8.7. `DELETE /api/v1/messages/{messageId}`

Muc dich:
- xoa message khoi thread cua current user

### Response

```json
{
  "messageId": "msg-001",
  "conversationId": "conv-001",
  "deletedForMe": true
}
```

Quy tac:
- day la `delete for me`, khong xoa message goc khoi conversation
- khong phat thread event chung vi thay doi nay la user-specific
- frontend bo bubble local sau REST success
- lan `GET /api/v1/conversations/{conversationId}` sau do se khong con tra message nay cho user da xoa
- backend van phat inbox update cho current user de dong bo preview/unread neu can

## 8.8. `PATCH /api/v1/messages/{messageId}`

Muc dich:
- sua noi dung text cua message da gui

### Request

```json
{
  "content": "Noi dung moi"
}
```

### Response

Tra ve `MessageResponse` da cap nhat.

Quy tac:
- chi nguoi gui moi duoc sua
- khong sua `SYSTEM` message
- khong sua message da thu hoi
- scope hien tai chi ho tro sua tin nhan co `content` text
- sau khi sua:
  - giu nguyen `message.id`
  - cap nhat `content`
  - cap nhat `editedTimeStamp`
- backend phat thread event va inbox event voi cung `message.id`

## 8.9. `DELETE /api/v1/messages/{messageId}/everyone`

Muc dich:
- xoa message cho toan bo participant

### Response

Tra ve `MessageResponse` da cap nhat.

Quy tac:
- chi nguoi gui moi duoc xoa cho moi nguoi
- khong xoa `SYSTEM` message
- khong dung flow nay cho poll message; poll dung endpoint xoa poll rieng
- neu da xoa roi thi request la idempotent
- backend tra payload da duoc chuan hoa:
  - `content = "Tin nhan da bi xoa"`
  - `deleted = true`
  - `deletedForEveryone = true`
  - `deletedTimeStamp != null`
- `attachments/stickers/reactions/reference/poll/call` bi xoa khoi payload hien thi
- backend phat thread event va inbox event voi cung `message.id`

## 9. `POST /api/v1/conversations/{conversationId}/typing`

Muc dich:
- phat typing indicator

### 9.1. Request

```json
{
  "typing": true
}
```

### 9.2. Response

Frontend khong can body bat buoc.

Quy tac:
- typing khong tao message
- typing khong cap nhat inbox summary

## 10. `/topic/inbox/{userId}`

Muc dich:
- dong bo lai conversation list

### 10.1. Payload

Uu tien:
- `ConversationSummaryResponse[]`

Neu payload chua on dinh:
- frontend co the bo qua body
- refetch `GET /api/v1/conversations`

### 10.2. Event trigger toi thieu

- co tin nhan moi
- direct conversation moi duoc tao
- co system event moi
- unread thay doi
- `lastMessage` thay doi

## 11. `/topic/conversations/{conversationId}`

Muc dich:
- dong bo message moi trong thread

### 11.1. Payload

Tra `MessageResponse`.

### 11.2. Quy tac

- `message.id` phai on dinh
- frontend dedupe theo `message.id`
- neu user dang mo thread va message den tu nguoi kia, frontend co the goi mark read ngay
- reaction/pin update cung di qua topic nay duoi dang `MessageResponse` update, frontend can upsert theo `message.id`

## 12. `/topic/typing/{conversationId}`

Muc dich:
- hien indicator dang go

### 12.1. Payload toi thieu

```json
{
  "conversationId": "conv-001",
  "userId": "user-002",
  "displayName": "Nguyen Van A",
  "typing": true
}
```

### 12.2. Quy tac

- frontend bo qua event cua chinh minh
- frontend duoc phep auto-clear indicator sau vai giay

## 12.3. Group blocked state

Backend can chot requester-specific blocked behavior cho group:
- chi `OWNER` va `ADMIN` moi duoc block/unblock member
- blocked member van thay group trong inbox/list
- blocked member khong duoc xem thread content
- blocked member khong duoc xem group detail/member detail/bulletin/archive
- blocked member khong duoc gui message hay thao tac trong group

Suggested minimal detail response khi requester bi block:

```json
{
  "conversationId": "group-001",
  "type": "GROUP",
  "groupName": "Nhom hoc tap",
  "groupAvatarUrl": "https://...",
  "blocked": true,
  "blockedMessage": "Ban da bi chan khoi nhom nay"
}
```

## 13. Quy tac on dinh contract

- `message.id` phai unique va on dinh
- `conversationId` phai on dinh
- frontend duoc phep chap nhan mot so alias field de chuyen doi qua giai doan on dinh hoa backend
- khi contract da chot, backend nen giam alias de tranh parse phuc tap

## 13.1. Error behavior toi thieu cho phase 1

Frontend co the ky vong:
- `400 Bad Request` cho payload khong hop le
- `404 Not Found` cho conversation/user profile/member khong ton tai

Response loi toi thieu:

```json
{
  "status": 400,
  "message": "Du lieu gui len khong hop le"
}
```

Hoac:

```json
{
  "status": 404,
  "message": "Khong tim thay user profile: user-002"
}
```

Quy tac phase 1:
- `POST /api/v1/messages/direct` voi `targetUserId` khong ton tai -> `404`
- `POST /api/v1/messages` hoac `POST /api/v1/messages/direct` voi `mentions` khong nam trong conversation -> `404`
- `POST /api/v1/messages` voi payload khong co `content/attachments/stickers/reference/poll/call` -> `400`

## 14. Checklist backend

- `GET /conversations` tra du summary de render sidebar
- `GET /conversations/{id}` tra du thread payload de render message
- `POST /messages` va `POST /messages/direct` tra `MessageResponse`
- `PUT /read` co tac dong unread ro rang
- `POST /typing` phat typing event
- STOMP inbox/thread/typing dung dung topic da chot

## 15. Checklist frontend

- parse duoc array va mot so envelope shape o `GET /conversations`
- khong suy luan render chi tu `type`
- dedupe message theo `message.id`
- inbox event co the refetch list thay vi tu merge tung field
- thread event phai map ve cung `MessageResponse` contract
