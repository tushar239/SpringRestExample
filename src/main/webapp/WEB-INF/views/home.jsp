<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>

<form action="http://localhost:8080/SpringRestExample/welcome/oneFormToAnother">
	Name: <input type="text" name="userName" value="${command.userName}" />
	Email: <input type="text" name="emailId" value="${command.emailId}"/>
	<button type="submit">submit</button>
</form>
</body>
</html>
