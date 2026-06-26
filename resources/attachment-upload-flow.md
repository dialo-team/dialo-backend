# Flow upload attachment cho messaging

Tai lieu nay chot flow upload attachment de frontend co the gui `image` va `file` that trong messaging thay vi chi optimistic local.

Tai lieu lien quan:
- esources/conversation-data-loading-flow.md`
- esources/conversation-realtime-flow.md`

## 0. Pham vi tai lieu

Tai lieu nay chi dong cho:
- upload image
- upload file
- tao `attachments[]` hop le de gui vao message `DEFAULT`
- ket hop `content + attachments` trong cung mot message

Tai lieu nay chua mo ta:
- upload video / audio tach rieng
- thumbnail / placeholder generation chi tiet
- virus scan / DLP / permission sau hon
- revoke attachment

Ket luan pham vi:
- Tai lieu nay du de frontend lam send `image/file` that.
- Tai lieu nay khong thay the flow send/realtime tong the trong esources/conversation-realtime-flow.md`.

## 1. Nguon su that

Ket noi hien tai cho frontend:
- REST API van di qua gateway http://localhost:9000
- WebSocket/STOMP khong di qua gateway MVC hien tai, ma di thang ws://localhost:8085/ws-messaging

## 1. Nguon su that

- Frontend khong duoc gui truc tiep `File` object vao `POST /api/v1/messages` hoac `POST /api/v1/messages/direct`
- Frontend phai upload file truoc de nhan duoc metadata attachment hop le
- Sau khi upload thanh cong, frontend moi gui message voi `attachments[]`

Tom tat:
1. upload file
2. nhan `attachment descriptor`
3. gui message `DEFAULT`

## 2. Kieu du lieu attachment can co trong message

Moi phan tu trong `attachments[]` can toi thieu:

```json
{
  "id": "attachment-id",
  "fileName": "demo.png",
  "contentType": "image/png",
  "size": 245123,
  "url": "https://dialo-messaging.s3.ap-southeast-1.amazonaws.com/messaging/attachments/attachment-id.png",
  "width": 1280,
  "height": 720
}
```

Luu y:
- voi file thuong, `width` va `height` co the la `null`
- voi image, backend tra them `width`, `height` neu doc duoc
- `url` la public URL cua object tren AWS S3

## 3. Flow tong quat

### 3.1. User chon image / file
- Frontend mo file picker
- User chon 1 hoac nhieu file
- Frontend tao preview local de render optimistic

### 3.2. Frontend upload file
- Frontend goi API upload attachment
- Moi file duoc upload va backend tra metadata attachment

### 3.3. Frontend gui message
- Sau khi da co day du `attachments[]`, frontend goi:
  - `POST /api/v1/messages` voi conversation da ton tai
  - hoac `POST /api/v1/messages/direct` neu la direct message dau tien

### 3.4. Backend luu message
- Backend luu message thong thuong voi `type = DEFAULT`
- `content` co the rong neu day la message chi co attachment
- `attachments[]` duoc luu vao message

### 3.5. Backend phat realtime
- Phat message moi vao `/topic/conversations/{conversationId}`
- Phat inbox summary moi vao `/topic/inbox/{userId}`

## 4. API upload that da co

Request:

`POST /api/v1/attachments`

Header frontend gui len gateway:

```http
Authorization: Bearer <accessToken>
Content-Type: multipart/form-data
```

Header noi bo sau gateway forward sang messaging:

```http
X-User-Id: <current-user-id>
```

Form data:
- `files`: 1 hoac nhieu file

Response:

```json
{
  "items": [
    {
      "id": "attachment-001",
      "fileName": "demo.png",
      "contentType": "image/png",
      "size": 245123,
      "url": "https://dialo-messaging.s3.ap-southeast-1.amazonaws.com/messaging/attachments/attachment-001.png",
      "width": 1280,
      "height": 720
    }
  ]
}
```

Behavior hien tai:
- file duoc upload len AWS S3
- backend tra `url` S3/public URL de frontend dung ngay
- image co them `width`, `height` neu doc duoc metadata

## 5. Flow gui image / file trong conversation da co

### 5.1. Image only

1. user chon image
2. frontend upload image
3. frontend nhan `attachments[]`
4. frontend goi:

`POST /api/v1/messages`

```json
{
  "conversationId": "conversation-id",
  "content": "",
  "attachments": [
    {
      "id": "attachment-001",
      "fileName": "demo.png",
      "contentType": "image/png",
      "size": 245123,
      "url": "https://dialo-messaging.s3.ap-southeast-1.amazonaws.com/messaging/attachments/attachment-001.png",
      "width": 1280,
      "height": 720
    }
  ],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null
}
```

Ket qua:
- backend luu `type = DEFAULT`
- frontend render message kieu image dua tren `attachments`

### 5.2. File only

Tuong tu image, chi khac `contentType` va UI render file card.

### 5.3. Text + attachments

Frontend duoc phep gui:

```json
{
  "conversationId": "conversation-id",
  "content": "Gui ban tai lieu va hinh anh",
  "attachments": [
    {
      "id": "attachment-001",
      "fileName": "demo.png",
      "contentType": "image/png",
      "size": 245123,
      "url": "https://dialo-messaging.s3.ap-southeast-1.amazonaws.com/messaging/attachments/attachment-001.png"
    },
    {
      "id": "attachment-002",
      "fileName": "spec.pdf",
      "contentType": "application/pdf",
      "size": 450123,
      "url": "https://dialo-messaging.s3.ap-southeast-1.amazonaws.com/messaging/attachments/attachment-002.pdf"
    }
  ],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null
}
```

Ket qua:
- van la mot message `DEFAULT`
- frontend render trong cung mot item gom text + media/file

## 6. Flow gui attachment cho direct message dau tien

Neu chua co conversation va user gui image / file dau tien, frontend co 2 lua chon.

### Cach A: Tao conversation truoc

1. `POST /api/v1/conversations`
2. upload attachment
3. `POST /api/v1/messages`

### Cach B: Gui thang direct message dau tien

1. upload attachment
2. `POST /api/v1/messages/direct`

```json
{
  "targetUserId": "target-user-id",
  "content": "",
  "attachments": [
    {
      "id": "attachment-001",
      "fileName": "demo.png",
      "contentType": "image/png",
      "size": 245123,
      "url": "https://dialo-messaging.s3.ap-southeast-1.amazonaws.com/messaging/attachments/attachment-001.png"
    }
  ],
  "stickers": [],
  "reference": null,
  "poll": null,
  "call": null
}
```

Backend:
- tai su dung direct conversation neu da ton tai
- neu chua co thi tao moi
- luu message `DEFAULT`

## 7. Optimistic UI

Frontend nen:
- tao preview local ngay sau khi user chon file
- hien trang thai `uploading` roi `sending`
- khi upload + send thanh cong thi replace message optimistic bang message backend
- neu upload loi thi message optimistic chuyen sang loi hoac cho phep gui lai

Frontend khong nen:
- danh dau `sent` truoc khi upload va send message thanh cong

## 8. Xu ly loi

Can it nhat cac truong hop:
- file vuot qua gioi han dung luong
- content type khong hop le
- upload that bai
- tao message that bai sau khi upload xong

UI mong muon:
- hien thong bao loi gon
- cho phep user xoa item loi hoac gui lai

## 9. Realtime

Sau khi message attachment duoc luu:
- thread dang mo nhan `MessageResponse` moi qua `/topic/conversations/{conversationId}`
- inbox nhan refresh qua `/topic/inbox/{userId}`

Frontend render:
- image neu attachment co `contentType` bat dau bang `image/`
- file card neu attachment khong phai image
- text + attachment trong cung mot message item neu `content` khong rong

## 10. Quy tac backend da chot

- message co attachment van luu voi `type = DEFAULT`
- khong tach `IMAGE_MESSAGE`, `FILE_MESSAGE` rieng
- `attachments[]` trong response thread va realtime phai cung mot shape
- `attachments[]` trong request `POST /api/v1/messages` va `POST /api/v1/messages/direct` phai cung mot shape
- upload API that hien tai la `POST /api/v1/attachments`
- file duoc luu tren AWS S3 va `url` la public URL/backend-configured URL

## 11. Checklist frontend

- Chon image -> upload -> gui message `DEFAULT` voi `attachments[]`
- Chon file -> upload -> gui message `DEFAULT` voi `attachments[]`
- Gui `content + attachments` trong cung mot item
- Replace optimistic message bang message backend sau khi send thanh cong
- Nhan realtime thread va render dung image/file
- Refetch inbox khi nhan event inbox
