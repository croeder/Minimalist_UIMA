<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" />
	
	<xsl:preserve-space elements="sec kwd def-item abstract italic p  caption "/>

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

	<xsl:template match="table" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="entry" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="table/label" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>
	
	<xsl:template match="note-para" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="figure" priority='0'>
		<xsl:element name="FIGURE"> 
			<xsl:attribute name="NAME">
				<xsl:value-of select="label" />
			</xsl:attribute>
			<xsl:value-of select="caption" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="textbox" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

<!-- body  -->
	<xsl:template match="body" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="sections" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="section" priority='0'>
		<xsl:element name="SECTION"> 
			<xsl:attribute name="NAME">
				<xsl:value-of select="label" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="section-title" />
			</xsl:attribute>
		<xsl:apply-templates select="node()" />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" />
	</xsl:template>

	<xsl:template match="para" priority='0'>
		<xsl:value-of select="'&#xA;'" />
		<xsl:element name="PARAGRAPH"> 
<!-- good grief!!! This causes problems because it selects the text from the list-items as well preventing me from dealing with them separately -->
		<xsl:value-of select="." />
		<xsl:apply-templates select="node()" />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" />
	</xsl:template>

	<xsl:template match="list-item/label" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="list-item/para" priority='0'>
		<xsl:text> </xsl:text>
		<xsl:value-of select="." />
		<xsl:text> </xsl:text>
	</xsl:template>

	<xsl:template match="nomenclature" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="acknowledgment" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="appendices" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>


	<xsl:template match="italic" priority='0'>
		<xsl:element name="ITALICS"> 
		<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

<!-- head -->	
	<xsl:template match="head" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="abstract" priority='0'>
		<xsl:element name="ABSTRACT"> 
		<xsl:value-of select="." />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" />
	</xsl:template>

	<xsl:template match="dochead" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="keywords" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="title" priority='0'>
		<xsl:element name="TITLE"> 
		<xsl:value-of select="." />
		</xsl:element>
		<xsl:value-of select="'&#xA;'" />
	</xsl:template>

<!-- tail -->
	<xsl:template match="tail" priority='0'>
		<xsl:apply-templates select="node()" />
	</xsl:template>

	<xsl:template match="glossary" priority='0'>
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="bibliography">
	</xsl:template>

	
</xsl:stylesheet>
