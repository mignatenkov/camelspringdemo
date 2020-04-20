<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        ID_ART,NAME,CODE,USERNAME,GUID
        <xsl:for-each select="articles/article">
            <xsl:value-of select="@id_art" />, <xsl:value-of select="@name" />, <xsl:value-of select="@code" />, <xsl:value-of select="@username" />, <xsl:value-of select="@guid" />
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>