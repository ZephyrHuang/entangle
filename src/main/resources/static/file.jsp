<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
    <body>
    <h1>文件上传</h1>
    <form action="${pageContext.request.contextPath}/view/file/upload/" method="post" enctype="multipart/form-data">
        <input type="file" name="选择文件"/>
        <input type="submit" name="上传"/>
    </form>
    </body>
</html>
