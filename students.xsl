<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="/">
    <fo:root>
        <fo:layout-master-set>
            <fo:simple-page-master master-name="A4" page-width="210mm" page-height="297mm">
                <fo:region-body margin="2cm"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        <fo:page-sequence master-reference="A4">
            <fo:flow flow-name="xsl-region-body">
                <fo:block font-size="18pt" text-align="center" margin-bottom="1cm">
                    Studentenliste
                </fo:block>
                
                <fo:table table-layout="fixed" width="100%">
                    <fo:table-column column-width="20%"/>
                    <fo:table-column column-width="30%"/>
                    <fo:table-column column-width="30%"/>
                    <fo:table-column column-width="20%"/>
                    
                    <fo:table-header>
                        <fo:table-row font-weight="bold">
                            <fo:table-cell border="1pt solid black"><fo:block>ID</fo:block></fo:table-cell>
                            <fo:table-cell border="1pt solid black"><fo:block>Name</fo:block></fo:table-cell>
                            <fo:table-cell border="1pt solid black"><fo:block>Email</fo:block></fo:table-cell>
                            <fo:table-cell border="1pt solid black"><fo:block>Note</fo:block></fo:table-cell>
                        </fo:table-row>
                    </fo:table-header>
                    
                    <fo:table-body>
                        <xsl:for-each select="students/student">
                            <fo:table-row>
                                <fo:table-cell border="1pt solid black">
                                    <fo:block><xsl:value-of select="@id"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black">
                                    <fo:block><xsl:value-of select="name"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black">
                                    <fo:block><xsl:value-of select="email"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black">
                                    <fo:block><xsl:value-of select="grade"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </xsl:for-each>
                    </fo:table-body>
                </fo:table>
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</xsl:template>

</xsl:stylesheet>