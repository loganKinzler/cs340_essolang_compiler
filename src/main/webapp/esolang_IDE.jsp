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
        <div id="program-input">
            <textarea id="esolang-editor" rows="28" cols="85" name="editor-text" style="resize: none;"></textarea>

            <div id="input-buttons">
                <button onclick="compile()">Compile</button>
                <button onclick="run()">Run</button>
            </div>

            <div id="graphics-output"></div>
        </div>

        <div id="program-output">
            <textarea id="trace-output" readonly rows="12" cols="85" name="output-text" style="resize: none;"></textarea>
        </div>
    </body>
</html>