# Flow lay du lieu cuoc tro chuyen

Tai lieu nay chot flow lay du lieu cho conversation list va conversation thread o muc toi thieu de frontend noi voi `services/messaging`.

Tai lieu lien quan:
- `docs/friend-request-flow.md`
- `docs/conversation-realtime-flow.md`
- `docs/message-realtime-flow.md`
- `docs/conversation-api-contract.md`

## 0. Pham vi tai lieu

Tai lieu nay chi dong cho flow lay du lieu conversation toi thieu:
- bootstrap danh sach cuoc tro chuyen
- mo thread cua 1 conversation
- tao hoac tai su dung direct conversation
- mark read
- dong bo lai inbox khi co event realtime

Tai lieu nay chua mo ta day du:
- group conversation flow toan dien
- pagination / cursor chinh thuc
- search conversation tren backend
- sort / filter conversation tren backend
- contract loi chi tiet

Ket luan pham vi:
- Tai lieu nay du de frontend lam `conversation list + direct thread + inbox refresh`.
- Tai lieu nay khong mo ta day du message payload, typing, hay send message contract. Cac phan do nam trong `docs/message-realtime-flow.md`.

## 1. Nguon su that

Ket noi hien tai cho frontend:
- REST API trong tai lieu nay di qua gateway http://localhost:9000
- Topic realtime duoc subscribe qua STOMP/WebSocket noi thang ws://localhost:8085/ws-messaging
- Gateway validate `Authorization` va tu lay user id tu access token de gan `X-User-Id` khi forward request vao messaging

## 1. Nguon su that

- Sidebar conversation list lay tu `GET /api/v1/conversations`
- Thread lay tu `GET /api/v1/conversations/{conversationId}`
- Hanh dong mo direct chat lay tu `POST /api/v1/conversations`
- Trang thai da doc dong bo qua `PUT /api/v1/conversations/{conversationId}/read`

Neu frontend dang co session hop le va backend tra du lieu thanh cong thi khong duoc tron conversation mock vao sidebar hoac thread bootstrap.

## 2. Bootstrap danh sach cuoc tro chuyen

Khi nguoi dung vao man `Conversations`, frontend goi:

`GET /api/v1/conversations`

Header frontend gui len gateway:

```http
Authorization: Bearer <accessToken>
```

Header noi bo sau gateway forward sang messaging:

```http
X-User-Id: <current-user-id>
```

Muc dich:
- lay conversation summary de render sidebar
- khong lay full thread tai buoc nay

Moi item toi thieu nen co:

```json
{
  "conversationId": "conv-001",
  "type": "DIRECT",
  "counterpartId": "user-002",
  "counterpartName": "Nguyen Van A",
  "counterpartAvatarUrl": "https://...",
  "lastMessageId": "msg-999",
  "lastMessage": "Xin chao",
  "lastMessageSenderId": "user-002",
  "lastMessageType": "DEFAULT",
  "lastMessageAt": "2026-05-30T09:10:00Z",
  "lastMessageSystem": false,
  "unreadCount": 3,
  "unreadDisplay": "3"
}
```

Frontend map len UI:
- `conversationId` -> `conversation.id`
- `type` -> `conversation.type`
- `counterpartId` -> `conversation.counterpartId`
- `counterpartName` / `groupName` -> `conversation.name`
- `counterpartAvatarUrl` / `groupAvatarUrl` -> avatar
- `lastMessage` -> preview
- `lastMessageAt` -> thu tu sap xep
- `unreadCount` -> badge chua doc

Luu y:
- voi tin nhan thong thuong, `lastMessageType` thuong la `DEFAULT`
- frontend khong nen suy luan kieu preview chi tu `lastMessageType`
- neu can render preview thong minh hon, frontend phai dua vao du lieu thread hoac payload realtime moi nhat

## 3. Quy tac hien thi conversation list

- Danh sach sap xep giam dan theo `lastMessageAt`
- Conversation moi co hoat dong gan nhat phai len dau
- `unreadCount > 0` thi hien badge
- Neu `lastMessage` la system event moi nhat thi preview duoc phep hien text system event

Truong hop API tra rong:
- frontend hien sidebar rong
- frontend khong tu sinh mock conversation
- direct conversation chi xuat hien khi user chu dong bam `Nhan tin` va backend tra `conversationId`

## 4. Mo thread cua mot conversation

Khi nguoi dung chon 1 item trong sidebar, frontend goi:

`GET /api/v1/conversations/{conversationId}`

Header frontend gui len gateway:

```http
Authorization: Bearer <accessToken>
```

Header noi bo sau gateway forward sang messaging:

```http
X-User-Id: <current-user-id>
```

Response toi thieu:

```json
{
  "conversationId": "conv-001",
  "counterpartId": "user-002",
  "counterpartName": "Nguyen Van A",
  "counterpartAvatarUrl": "https://...",
  "unreadCount": 3,
  "messages": [
    {
      "id": "msg-999",
      "conversationId": "conv-001",
      "senderId": "user-002",
      "senderName": "Nguyen Van A",
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
  ]
}
```

Frontend:
- render thread tu `messages`
- neu la direct conversation thi hien header theo `counterpartName` va `counterpartAvatarUrl`
- voi message `type = DEFAULT`, frontend render theo payload thuc te cua item
- sau khi mo thread thanh cong thi goi mark read

## 5. Mark read

Sau khi nguoi dung mo conversation, frontend goi:

`PUT /api/v1/conversations/{conversationId}/read`

Header frontend gui len gateway:

```http
Authorization: Bearer <accessToken>
```

Header noi bo sau gateway forward sang messaging:

```http
X-User-Id: <current-user-id>
```

Ket qua mong muon:
- unread cua conversation do ve `0`
- sidebar bo badge chua doc
- neu backend co realtime inbox thi cac view khac cung duoc dong bo

## 6. Tao hoac tai su dung direct conversation

Khi user bam `Nhan tin` tu:
- ket qua tim kiem so dien thoai
- profile user
- entry point direct message khac

Frontend goi:

`POST /api/v1/conversations`

Request:

```json
{
  "participantIds": ["current-user-id", "target-user-id"]
}
```

Header frontend gui len gateway:

```http
Authorization: Bearer <accessToken>
```

Header noi bo sau gateway forward sang messaging:

```http
X-User-Id: <current-user-id>
```

Behavior:
- neu da co direct conversation giua 2 ben thi backend tra conversation cu
- neu chua co thi backend tao moi

Response toi thieu:

```json
{
  "conversationId": "conv-001",
  "type": "DIRECT",
  "counterpartId": "user-002",
  "counterpartName": "Nguyen Van A",
  "counterpartAvatarUrl": "https://..."
}
```

Frontend sau do:
1. dua conversation nay vao state sidebar neu chua co
2. set `selectedConversationId`
3. goi `GET /api/v1/conversations/{conversationId}` de doc thread neu can

## 7. Realtime refresh conversation list

Frontend subscribe inbox topic:

`/topic/inbox/{userId}`

Khi nhan event inbox:
- frontend khong can tu cap nhat tung field neu payload khong du
- frontend refetch lai `GET /api/v1/conversations`

Muc tieu cua refetch:
- dong bo preview moi nhat
- dong bo `lastMessageAt`
- dong bo `unreadCount`
- dong bo thu tu conversation list

## 8. Realtime khi dang mo thread

Frontend subscribe thread topic:

`/topic/conversations/{conversationId}`

Khi nhan message moi trong thread dang mo:
- append message vao thread neu chua ton tai
- neu message do den tu nguoi kia thi frontend co the mark read ngay
- neu message co `type = DEFAULT` thi render theo payload thuc te cua item
- sau do refetch inbox hoac cap nhat sidebar de dong bo preview / unread / order

## 9. System event tro thanh ban be

Khi messaging consume `friend.accepted`:
- backend tao system message `Hai ban da tro thanh ban be`
- message nay thuoc direct conversation cua hai ben

Tac dong du lieu:
- neu conversation da ton tai thi event xuat hien trong thread
- neu chua co tin nhan nao truoc do thi conversation van co the xuat hien trong inbox voi preview la system event nay
- neu event nay la message moi nhat thi `lastMessage` / `lastMessageAt` cua inbox phai phan anh dung

## 10. Quy tac fallback

- Co session + goi API thanh cong -> chi dung data backend
- Khong co session hoac khong goi duoc backend -> moi duoc dung snapshot/mock local de phuc vu UI development
- Khong duoc tron mock vao data fetch that trong cung mot bootstrap

## 11. Checklist frontend

- Vao man conversations thi goi `GET /api/v1/conversations`
- Chon 1 conversation thi goi `GET /api/v1/conversations/{conversationId}`
- Mo thread xong thi goi `PUT /read`
- Bam `Nhan tin` voi direct chat thi goi `POST /api/v1/conversations`
- Co event `/topic/inbox/{userId}` thi refetch conversation list
- Co event `/topic/conversations/{conversationId}` thi append thread va dong bo inbox
