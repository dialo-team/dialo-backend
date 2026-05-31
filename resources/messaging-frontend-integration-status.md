# Messaging frontend integration status

Tai lieu nay chot tinh trang thuc te cua backend `services/messaging` de frontend biet:
- flow nao co the code va tich hop ngay
- flow nao da co contract REST/STOMP ro rang
- feature nao chua nen assume la production-ready

Tai lieu nay uu tien **code thuc te hien co** hon mong muon san pham.

Tai lieu lien quan:
- `resources/conversation-api-contract.md`
- `resources/conversation-data-loading-flow.md`
- `resources/conversation-realtime-flow.md`
- `resources/message-realtime-flow.md`
- `resources/message-rendering-contract.md`
- `resources/message-error-retry-flow.md`
- `resources/attachment-upload-flow.md`

## 1. Ket luan ngan

Backend messaging hien tai da mo duoc:
- direct conversation
- direct chat realtime
- group management
- group chat tren cung message flow
- message actions chinh
- poll management co ban
- call message/lifecycle co ban

Backend chua co ro rang cho:
- signaling/WebRTC session thoi gian thuc

## 2. Frontend co the code production ngay

### 2.1. Direct va group conversation
- `GET /api/v1/conversations`
- `GET /api/v1/conversations/{conversationId}`
- `POST /api/v1/conversations`
- `POST /api/v1/conversations/groups`
- `PUT /api/v1/conversations/{conversationId}/read`
- `POST /api/v1/conversations/{conversationId}/typing`

### 2.2. Message flow dung chung cho direct va group
- `POST /api/v1/messages`
- `POST /api/v1/messages/direct`
- `POST /api/v1/attachments`
- support:
  - text
  - attachment
  - image
  - sticker
  - reply
  - `content + attachments`
  - poll payload

### 2.3. Message actions
- `POST /api/v1/messages/{messageId}/reactions`
- `DELETE /api/v1/messages/{messageId}/reactions/{emoji}`
- `PUT /api/v1/messages/{messageId}/pin`
- `DELETE /api/v1/messages/{messageId}/pin`
- `PUT /api/v1/messages/{messageId}/revoke`
- `POST /api/v1/messages/{messageId}/forward`
- `DELETE /api/v1/messages/{messageId}`
- `DELETE /api/v1/messages/{messageId}/everyone`
- `PATCH /api/v1/messages/{messageId}`

### 2.4. Group management
- `POST /api/v1/conversations/{conversationId}/members`
- `DELETE /api/v1/conversations/{conversationId}/members/{memberUserId}`
- `PUT /api/v1/conversations/{conversationId}/members/{memberUserId}/role`
- `POST /api/v1/conversations/{conversationId}/leave`
- `DELETE /api/v1/conversations/{conversationId}`

### 2.5. Poll management
- `POST /api/v1/conversations/{conversationId}/polls`
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/votes`
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/options`
- `POST /api/v1/conversations/{conversationId}/polls/{messageId}/close`
- `DELETE /api/v1/conversations/{conversationId}/polls/{messageId}`
- `GET /api/v1/conversations/{conversationId}/polls/{messageId}/answers/{answerId}`

### 2.6. Call management
- `POST /api/v1/conversations/{conversationId}/calls`
- `POST /api/v1/conversations/{conversationId}/calls/{messageId}/end`

### 2.6. Realtime
- inbox: `/topic/inbox/{userId}`
- thread: `/topic/conversations/{conversationId}`
- typing: `/topic/typing/{conversationId}`

## 3. Quy tac frontend can bam vao

### 3.1. Auth
- REST: frontend chi gui `Authorization: Bearer <accessToken>` len gateway
- STOMP CONNECT: frontend chi gui `Authorization: Bearer <accessToken>`

### 3.2. Dedupe
- thread event va REST response deu phai upsert theo `message.id`
- reaction/pin/revoke/edit/poll update khong append bubble moi

### 3.3. Delete for me
- `DELETE /api/v1/messages/{messageId}` la user-specific
- khong co shared thread event
- frontend tu bo bubble local sau REST success

### 3.5. Delete for everyone
- `DELETE /api/v1/messages/{messageId}/everyone`
- response la `MessageResponse` update
- `revoke` va `delete for everyone` la 2 semantics khac nhau:
  - revoke -> `Tin nhan da duoc thu hoi`
  - delete for everyone -> `Tin nhan da bi xoa`

### 3.6. Read receipt
- `MessageResponse` hien tai co `readBy`
- `readBy` duoc suy ra tu `lastReadPosition/lastReadAt` cua membership
- `PUT /read` se phat thread update tren last-read message de UI cap nhat read receipt nhanh

### 3.4. Poll selected state
- `poll.results.selectedAnswerIds` co y nghia day du tren response REST requester-specific
- STOMP thread event uu tien de cap nhat aggregate count/finalized state

## 4. Mapping theo feature

### 4.1. Flow tro chuyen direct
- **Da co**

### 4.2. Flow nhan tin 1-1 realtime
- **Da co**

Da co:
- text
- file / image
- sticker / emoji theo `stickers`
- reply
- reaction
- pin / unpin
- revoke
- forward
- delete for me
- edit
- typing
- mark read
- realtime inbox
- realtime thread
- upload file

Chua co ro rang:
- player/video tile contract rieng

### 4.3. Flow quan ly nhom
- **Da co**

Da co:
- tao nhom
- them thanh vien
- xoa thanh vien
- gan quyen `ADMIN/MEMBER`
- chuyen `OWNER`
- roi nhom
- giai tan nhom

### 4.4. Chat nhom
- **Da co**

Group chat dung chung:
- `POST /api/v1/messages`
- `GET /api/v1/conversations/{conversationId}`
- `/topic/conversations/{conversationId}`
- `/topic/typing/{conversationId}`
- `PUT /read`

### 4.5. Quan ly binh chon
- **Da co ban**

Da co:
- tao poll
- vote
- them lua chon
- dong poll
- xoa poll
- xem danh sach voter theo dap an

Chua co:
- permission phuc tap hon nhu owner/admin group can thiep poll cua nguoi khac

### 4.6. Call domain
- **Da co ban**

Da co:
- tao call item trong thread
- end call item
- realtime thread update cho call state

Chua co:
- signaling/WebRTC session
- ringing/presence state rieng

## 5. Error behavior toi thieu

Frontend co the ky vong:
- `400` cho payload khong hop le, noi dung rong, self-DM, vote sai dap an, poll da dong
- `404` cho conversation/message/user profile/member khong ton tai

Response loi toi thieu:

```json
{
  "status": 400,
  "message": "Du lieu gui len khong hop le"
}
```

## 6. Nguon su that

Khi co mau thuan giua tai lieu mong muon va code:
- frontend uu tien file nay + `conversation-api-contract.md`
- chi xem feature la san sang khi da co endpoint + service logic + STOMP contract neu can
