<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>

<html>
    <head>
        <link href="css/IDEstyle.css" rel="stylesheet"/>
        
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        
        <script>const contextPath = "${pageContext.request.contextPath}"</script>
        <script src="js/esolang-editor.js"></script>
    </head>
    <body>
        <textarea id="esolang-editor" rows="10" cols="40" name="editor-text" style="resize: none;"></textarea>

        <button onclick="compile()">Compile</button>
        <button onclick="run()">Run</button>

        <div id="program-output"></div>
        <div id="trace-output"></div>
    </body>
</html>