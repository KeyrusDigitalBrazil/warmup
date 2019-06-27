<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
	window.onload = function() {

		${chatScript}

		if (typeof scr !== 'undefined') {
			scr.onload = function() {
				if (typeof cvc !== 'undefined') { // fail safe
					if ("${customerName}" !== '' && "${customerEmail}" !== '')
						cvc.setUser("${customerName}", "${customerEmail}");
				}
			};

		}
	}
</script>

