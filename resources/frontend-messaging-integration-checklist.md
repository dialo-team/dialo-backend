# Frontend messaging integration checklist

Tai lieu nay la checklist giao thang cho frontend de tich hop messaging theo trang thai backend hien tai.

Tai lieu chi tiet de doi chieu:
- `resources/conversation-api-contract.md`
- `resources/message-realtime-flow.md`
- `resources/message-rendering-contract.md`
- `resources/messaging-frontend-integration-status.md`
- `resources/frontend-messaging-ui-behavior-spec.md`
- `resources/messaging-phase1-backend-handoff.md`

## 1. Auth va ket noi

### REST
- gui `Authorization: Bearer <accessToken>` len gateway
- khong tu gui `X-User-Id`

### STOMP CONNECT
- gui `Authorization: Bearer <accessToken>`
- khong can gui `X-User-Id`

### Topic can subscribe
- inbox:
  - `/topic/inbox/{currentUserId}`
- khi mo thread:
  - `/topic/conversations/{conversationId}`
  - `/topic/typing/{conversationId}`

## 2. Inbox

### Bootstrap
- `GET /api/v1/conversations`

### Render
- sort giam dan theo `lastMessageAt`
- preview uu tien:
  - `lastMessage`
  - `lastMessagePreview`
- unread:
  - `unreadCount`

### Khi co inbox event
- refetch `GET /api/v1/conversations`

## 3. Thread

### Mo thread
- `GET /api/v1/conversations/{conversationId}`
- load xong goi:
  - `PUT /api/v1/conversations/{conversationId}/read`

### Dedupe rule bat buoc
- moi update thread phai upsert theo `message.id`
- khong append mu quang theo thu tu event

### Render field chinh trong `MessageResponse`
- `content`
- `attachments`
- `stickers`
- `reference`
- `poll`
- `call`
- `reactions`
- `readBy`
- `pinned`
- `editedTimeStamp`
- `deleted`
- `deletedForEveryone`

## 4. Direct conversation

### Tao/reuse direct
- `POST /api/v1/conversations`

### Gui first message direct
- `POST /api/v1/messages/direct`

### Gui message vao conversation da co
- `POST /api/v1/messages`

## 5. Message UI states

### Optimistic
- text
- sticker
- attachment
- reply

### Khong optimistic bubble
- mark read
- typing
- inbox refetch

### Retry
- bubble fail giu nguyen vi tri
- retry tren chinh bubble

## 6. Upload

### Upload file
- `POST /api/v1/attachments`

### Gui attachment message
- upload truoc
- dung attachment response de gui `POST /api/v1/messages`

## 7. Message actions

### Reaction
- add:
  - `POST /api/v1/messages/{messageId}/reactions`
- remove:
  - `DELETE /api/v1/messages/{messageId}/reactions/{emoji}`

Rule:
- thread event la upsert theo `message.id`
- khong append bubble moi

### Pin
- pin:
  - `PUT /api/v1/messages/{messageId}/pin`
- unpin:
  - `DELETE /api/v1/messages/{messageId}/pin`

### Revoke
- `PUT /api/v1/messages/{messageId}/revoke`

Rule render:
- placeholder:
  - `Tin nhan da duoc thu hoi`

### Delete for me
- `DELETE /api/v1/messages/{messageId}`

Rule render:
- xoa bubble local ngay sau REST success
- khong cho shared thread event

### Delete for everyone
- `DELETE /api/v1/messages/{messageId}/everyone`

Rule render:
- upsert bubble cu theo `message.id`
- placeholder:
  - `Tin nhan da bi xoa`

### Edit
- `PATCH /api/v1/messages/{messageId}`

Rule render:
- update in-place
- co the hien `da chinh sua`

### Forward
- `POST /api/v1/messages/{messageId}/forward`

Request dich den:
- `{ "conversationId": "..." }`
- hoac `{ "targetUserId": "..." }`

Rule render:
- thread nguon khong doi
- thread dich nhan message moi

## 8. Typing

### Send
- `POST /api/v1/conversations/{conversationId}/typing`

### Receive
- `/topic/typing/{conversationId}`

Rule UI:
- bo qua event cua chinh minh
- auto clear sau vai giay neu khong co event moi

## 9. Read receipt

### Nguon du lieu
- `MessageResponse.readBy`

### Rule UI
- co the render read receipt nho o bubble cuoi hoac bubble da doc
- direct:
  - co the hien avatar/read state cua ben kia
- group:
  - co the hien count hoac danh sach nho

### Realtime
- sau `PUT /read`, backend phat thread update tren last-read message

## 10. Group management

### Create group
- `POST /api/v1/conversations/groups`
- request tao nhom phai du de tao thanh 1 group co it nhat 3 nguoi tong cong, tinh ca requester

### Add member
- `POST /api/v1/conversations/{conversationId}/members`

### Remove member
- `DELETE /api/v1/conversations/{conversationId}/members/{memberUserId}`

### Change role
- `PUT /api/v1/conversations/{conversationId}/members/{memberUserId}/role`

### Transfer owner
- `PUT /api/v1/conversations/{conversationId}/owner`

### Leave group
- `POST /api/v1/conversations/{conversationId}/leave`

### Dissolve group
- `DELETE /api/v1/conversations/{conversationId}`

## 11. Group chat

Group chat dung chung message flow voi direct:
- `POST /api/v1/messages`
- `GET /api/v1/conversations/{conversationId}`
- `/topic/conversations/{conversationId}`
- `/topic/typing/{conversationId}`
- `PUT /read`

Frontend khong can tach rieng message engine cho direct va group.

## 12. Poll

### Create poll
- `POST /api/v1/conversations/{conversationId}/polls`

### Vote
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes`

### Add option
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/options`

### Close
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/close`

### Delete
- `DELETE /api/v1/conversations/{conversationId}/polls/{messageId}`

### Voter list
- `GET /api/v1/conversations/{conversationId}/polls/{messageId}/answers/{answerId}`

### Rule UI
- poll la mot bubble message
- vote/close/add option la upsert theo `message.id`
- `selectedAnswerIds` nen uu tien doc tu REST requester-specific response

## 13. Call

### Create call item
- `POST /api/v1/conversations/{conversationId}/calls`

### End call item
- `POST /api/v1/conversations/{conversationId}/calls/{messageId}/end`

### Field can render
- `call.type`
- `call.status`
- `call.users`
- `call.startedTime`
- `call.endedTime`

### Gioi han hien tai
- day la call message/lifecycle contract
- chua bao gom signaling/WebRTC session

## 14. Placeholder text can hardcode theo contract

- revoke:
  - `Tin nhan da duoc thu hoi`
- delete for everyone:
  - `Tin nhan da bi xoa`
- sidebar preview image:
  - `Anh`
- sidebar preview sticker:
  - `Sticker`
- sidebar preview file:
  - `File - <ten file>`

## 15. Thu tu tich hop de tranh vo state

1. auth REST + STOMP
2. inbox bootstrap
3. thread bootstrap
4. direct send text
5. attachment/sticker/reply
6. dedupe thread event
7. reaction/pin/edit/revoke/delete
8. read receipt
9. group management
10. group chat
11. poll
12. call item

## 16. Phan chua trong pham vi hien tai

- signaling/WebRTC session
- player/video tile contract rieng
