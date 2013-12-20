#!/bin/bash

# http://stackoverflow.com/questions/10765243/how-can-i-rewrite-this-curl-multipart-form-data-request-without-using-f
echo "post multipart"
curl -X POST -H "Content-Type: multipart/form-data; boundary=------------------------------4ebf00fbcf09" \
--data-binary @test.txt http://localhost:8080/myapp/file/upload


echo ""
echo "get"
curl  http://localhost:8080/myapp/conceptmapper
echo ""

