/*************************************************************************
 *                                                                       *
 *  CESeCore: CE Security Core                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.cesecore.certificates.certificatetransparency;

import java.security.cert.Certificate;
import java.util.List;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.cesecore.certificates.certificateprofile.CertificateProfile;

/**
 * X509CA will attempt to load org.cesecore.certificates.certificatetransparency.CertificateTransparencyImpl
 * that must implement this interface if it exists.
 *
 * The reason why this is an interface is because the CT support is a separate feature not
 * included in standard EJBCA.
 *
 * @version $Id$
 */
public interface CertificateTransparency {

    String SCTLIST_OID = "1.3.6.1.4.1.11129.2.4.2";

    /**
     * Controls which parameters are used for minimum and maximum number of SCTs.
     * Also, for OCSP and PUBLISHER, the {@link CertificateProfile#isUseCTSubmitExisting}
     * will be respected.
     */
    public static enum UsageMode {
        /** Requesting a SCT for a new certificate to be issued */
        CERTIFICATE,
        /** Requesting a SCT for inclusion in a OCSP response */
        OCSP,
        /** Publishing a certificate to all CT logs */
        PUBLISHER;
    };

    /**
     * Overloaded method with usageMode = UsageMode.CERTIFICATE.
     * 
     * @throws CTLogException If too many servers are down to satisfy the certificate profile.
     * @see CertificateTransparency#fetchSCTList(List, CertificateProfile, CTSubmissionConfigParams, UsageMode)
     */
    byte[] fetchSCTList(List<Certificate> chain, CertificateProfile certProfile, CTSubmissionConfigParams config) throws CTLogException;

    /**
     * Tries to add a certificate to CT logs and obtain SCTs (Signed Certificate Timestamps).
     * The configuration is taken from the certificate profile.
     *
     * @param chain Certificate chain including any CT signer and the leaf pre-certificate
     * @param certProfile Certificate profile with CT configuration
     * @param config Configuration parameters, that are not specific to the certificate profile.
     * @param usageMode Why we are fetching SCTs. The minimum and maximum number of SCTs are different depending on this.
     * @return A "SCT List" structure, for inclusion in e.g. the CT certificate extension, or null if no logs have been configured.
     * @throws CTLogException If too many servers are down to satisfy the certificate profile.
     */
    byte[] fetchSCTList(List<Certificate> chain, CertificateProfile certProfile, CTSubmissionConfigParams config, UsageMode usageMode) throws CTLogException;

    /**
     * Adds a critical extension to prevent the certificate from being used
     */
    void addPreCertPoison(X509v3CertificateBuilder precertbuilder);

    /**
     * Returns true if the given certificate has an SCT extension with at least one entry.
     */
    boolean hasSCTs(Certificate cert);

    /**
     * Clears the URL availability status (fast fail) cache.
     * To clear the OCSP CT cache, use OcspExtensionsCache.reloadCache()
     */
    void clearCaches();

}
