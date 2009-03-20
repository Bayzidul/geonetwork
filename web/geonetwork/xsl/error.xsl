<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="main.xsl"/>

	<!--
	page content
	-->
	<xsl:template name="content">
		<div id="error">
			<h2><xsl:value-of select="/root/gui/error/heading"/></h2>
			<p id="error"><xsl:value-of select="/root/gui/error/message"/></p>
			<p id="stacktrace"><xsl:value-of select="/root/error/class"/> : <xsl:value-of select="/root/error/message"/></p>
			<p><button id="goBack" class="content" onclick="JavaScript:goBack()"><xsl:value-of select="/root/gui/strings/backToPreviousPage"/></button></p>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
