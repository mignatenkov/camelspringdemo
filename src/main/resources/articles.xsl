<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <articles>
        <xsl:for-each select="articles/article">
            <xsl:param name="id_art" select="id_art" />
            <xsl:param name="name" select="name" />
            <xsl:param name="code" select="code" />
            <xsl:param name="username" select="username" />
            <xsl:param name="guid" select="guid" />
            <article id_art="{$id_art}" name="{$name}" code="{$code}" username="{$username}" guid="{$guid}" />
        </xsl:for-each>
        </articles>
    </xsl:template>

</xsl:stylesheet>