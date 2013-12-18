<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ce="http://www.elsevier.com/xml/common/dtd">
	<xsl:output method="xml" encoding="UTF-8" />
	

	<xsl:template match="/|@*|node()">
		<xsl:apply-templates select="node()"/>
	</xsl:template>
	
	<xsl:template match="article" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:element name="DOC"> 
			<xsl:apply-templates select="node()" />
		</xsl:element>
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="ce:table" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="ce:entry" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="ce:table/ce:label" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="ce:note-para" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="ce:figure" priority='0'>
		<xsl:element name="FIGURE"> 
			<xsl:attribute name="NAME">
				<xsl:value-of select="label" />
			</xsl:attribute>
			<xsl:value-of select="ce:caption" />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" /> 
	</xsl:template>

	<xsl:template match="ce:textbox" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

<!-- body  -->
	<xsl:template match="ce:body" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="ce:sections" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="ce:section" priority='0'>
		<xsl:element name="SECTION"> 
			<xsl:attribute name="NAME">
				<xsl:value-of select="ce:label" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="ce:section-title" />
			</xsl:attribute>
			<xsl:apply-templates select="node()" />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" /> 
	</xsl:template>

	<xsl:template match="ce:para" priority='0'>
		<xsl:value-of select="'&#xA;'" />
		<xsl:element name="PARAGRAPH"> 
<!-- good grief!!! This causes problems because it selects the text from the list-items as well preventing me from dealing with them separately -->
		<xsl:value-of select="." />
		<xsl:apply-templates select="node()" />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" /> 
	</xsl:template>

	<xsl:template match="ce:list-item/ce:label" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="ce:list-item/ce:para" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="ce:nomenclature" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:acknowledgment" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:appendices" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>


	<xsl:template match="ce:italic" priority='0'>
		<xsl:element name="ITALICS"> 
		<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

<!-- head -->	
	<xsl:template match="head" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="ce:abstract" priority='0'>
		<xsl:element name="ABSTRACT"> 
		<xsl:value-of select="." />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" /> 
	</xsl:template>

	<xsl:template match="ce:dochead" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:keywords" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:title" priority='0'>
		<xsl:element name="TITLE"> 
		<xsl:value-of select="." />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" />
	</xsl:template>
<!-- tail -->
	<xsl:template match="tail" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="ce:glossary" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="ce:bibliography">
	</xsl:template>

</xsl:stylesheet>
