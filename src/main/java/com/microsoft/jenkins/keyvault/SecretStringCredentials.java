/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */
package com.microsoft.jenkins.keyvault;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.cloudbees.plugins.credentials.CredentialsScope;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SecretStringCredentials extends BaseSecretCredentials implements StringCredentials {

    private static final long serialVersionUID = 1L;

    @DataBoundConstructor
    public SecretStringCredentials(CredentialsScope scope,
                                   String id,
                                   String description,
                                   String servicePrincipalId,
                                   String secretIdentifier) {
        super(scope, id, description, servicePrincipalId, secretIdentifier);
    }

    @NonNull
    @Override
    public Secret getSecret() {
        final KeyVaultSecret secretBundle = getKeyVaultSecret();
        return Secret.fromString(secretBundle.getValue());
    }

    @Extension
    public static class DescriptorImpl extends BaseSecretCredentials.DescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.String_Credentials_Diaplay_Name();
        }

        public FormValidation doVerifyConfiguration(
                @QueryParameter String servicePrincipalId,
                @QueryParameter String secretIdentifier) {

            final SecretStringCredentials credentials = new SecretStringCredentials(
                    CredentialsScope.SYSTEM, "", "", servicePrincipalId, secretIdentifier);

            try {
                credentials.getSecret();
            } catch (Exception e) {
                String message = e.getMessage();
                if (message == null) {
                    message = Messages.String_Credentials_Validation_Invalid();
                }
                return FormValidation.error(message);
            }

            return FormValidation.ok(Messages.String_Credentials_Validation_OK());
        }

    }
}
