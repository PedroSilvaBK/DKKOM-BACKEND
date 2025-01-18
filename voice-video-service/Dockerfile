FROM golang:1.23.1 as builder

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .

RUN apt-get update && apt-get upgrade -y && apt-get clean

# Build a statically linked binary
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app .

# Stage 2: Create a minimal image to run the Go application
FROM alpine:latest

WORKDIR /root/
COPY --from=builder /app/app .

EXPOSE 9999
CMD ["./app"]
