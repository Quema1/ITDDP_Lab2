<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fc="https://it.nure.ua/sport">
    <xsl:output method="html" encoding="UTF-8"/>

    <!-- Template for the root element -->
    <xsl:template match="/">
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="style.css"/>
            </head>
            <body>
                <div class="columns">
                    <div>
                        <label for="toggler-id-1">Clients [Click to expand]</label>
                        <input type="checkbox" id="toggler-id-1" class="toggler" />
                        <div class="toggler-content">
                            <h2>List of Clients:</h2>
                            <ul>
                                <!-- Apply templates to clients -->
                                <xsl:apply-templates select="//fc:client"/>
                            </ul>
                        </div>
                    </div>
                    <div>
                        <label for="toggler-id-2">Coaches [Click to expand]</label>
                        <input type="checkbox" id="toggler-id-2" class="toggler" />
                        <div class="toggler-content">
                            <h2>List of Coaches:</h2>
                            <ul>
                                <!-- Apply templates to coaches -->
                                <xsl:apply-templates select="//fc:coach"/>
                            </ul>
                        </div>
                    </div>
                    <div>
                        <label for="toggler-id-3">Subscriptions [Click to expand]</label>
                        <input type="checkbox" id="toggler-id-3" class="toggler" />
                        <div class="toggler-content">
                            <h2>List of Subscriptions:</h2>
                            <ul>
                                <!-- Apply templates to subscriptions -->
                                <xsl:apply-templates select="//fc:currentSubscription"/>
                            </ul>
                        </div>
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>

    <!-- Template for client -->
    <xsl:template match="fc:client">
        <li>
            <strong>Name:</strong> <xsl:value-of select="fc:name"/><br/>
            <strong>Surname:</strong> <xsl:value-of select="fc:surname"/><br/>
            <strong>Photo:</strong> <xsl:value-of select="fc:photo"/><br/>
            <strong>Date of Birth:</strong> <xsl:value-of select="fc:dateOfBirth"/><br/>

            <strong>Current Subscription:</strong>
            <ul>
                <xsl:apply-templates select="fc:currentSubscription"/>
            </ul>
        </li>
    </xsl:template>

    <!-- Template for currentSubscription -->
    <xsl:template match="fc:currentSubscription">
        <li>
            <strong>Price:</strong> <xsl:value-of select="fc:price"/><br/>
            <strong>Start Date:</strong> <xsl:value-of select="fc:startDate"/><br/>
            <strong>End Date:</strong> <xsl:value-of select="fc:endDate"/><br/>

            <strong>Coach:</strong>
            <ul>
                <xsl:apply-templates select="fc:coach"/>
            </ul>
        </li>
    </xsl:template>

    <!-- Template for coach -->
    <xsl:template match="fc:coach">
        <li>
            <strong>Name:</strong> <xsl:value-of select="fc:name"/><br/>
            <strong>Surname:</strong> <xsl:value-of select="fc:surname"/><br/>
            <strong>Experience:</strong> <xsl:value-of select="fc:experience"/><br/>
        </li>
    </xsl:template>

</xsl:stylesheet>