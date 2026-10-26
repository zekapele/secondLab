<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:c="http://www.example.com/candy"
                exclude-result-prefixes="c">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:key name="byKind" match="c:candy" use="c:type/c:kind"/>

    <xsl:template match="/">
        <Candy>
            <xsl:for-each select="//c:candy[generate-id() = generate-id(key('byKind', c:type/c:kind)[1])]">
                <xsl:sort select="c:type/c:kind"/>
                <xsl:element name="{c:type/c:kind}">
                    <xsl:apply-templates select="key('byKind', c:type/c:kind)"/>
                </xsl:element>
            </xsl:for-each>
        </Candy>
    </xsl:template>

    <xsl:template match="*">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:attribute name="{local-name()}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>
