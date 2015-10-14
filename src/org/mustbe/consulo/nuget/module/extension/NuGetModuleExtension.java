/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.nuget.module.extension;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.consulo.lombok.annotations.LazyInstance;
import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.nuget.dom.NuGetConfigFile;
import org.mustbe.consulo.nuget.dom.NuGetPackagesFile;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;

/**
 * @author VISTALL
 * @since 24.11.14
 */
public class NuGetModuleExtension extends ModuleExtensionImpl<NuGetModuleExtension>
{
    public static final String NUGET_CONFIG = "NuGet.Config";
    public static final String PACKAGES_CONFIG = "packages.config";

    private static final String NUGET_CONFIG_FILE_URL_KEY = "config-file-url";
    private static final String PACKAGES_FILE_URL_KEY = "packages-file-url";

    private static final int NUGET_CONFIG_SEARCH_DEPTH = 3;

    protected String myNuGetConfigFileUrl;
    protected String myPackagesConfigFileUrl;

	public NuGetModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@Override
	public void commit(@NotNull NuGetModuleExtension mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);
        myNuGetConfigFileUrl = StringUtil.nullize(mutableModuleExtension.myNuGetConfigFileUrl, true);
        myPackagesConfigFileUrl = StringUtil.nullize(mutableModuleExtension.myPackagesConfigFileUrl, true);
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
        if(myNuGetConfigFileUrl != null)
        {
            element.setAttribute(NUGET_CONFIG_FILE_URL_KEY, myNuGetConfigFileUrl);
        }
		if(myPackagesConfigFileUrl != null)
		{
            element.setAttribute(PACKAGES_FILE_URL_KEY, myPackagesConfigFileUrl);
		}
	}

	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
        myNuGetConfigFileUrl = element.getAttributeValue(NUGET_CONFIG_FILE_URL_KEY);
        myPackagesConfigFileUrl = element.getAttributeValue(PACKAGES_FILE_URL_KEY);
	}

	@NotNull
	@LazyInstance
	public NuGetRepositoryWorker getWorker()
	{
		return new NuGetRepositoryWorker(NuGetModuleExtension.this);
	}

    @Nullable
    public VirtualFile getNuGetConfigVirtualFile()
    {
        return getModuleVirtualFileBySpecifiedPathAndFallbackName(myNuGetConfigFileUrl, NUGET_CONFIG);
    }

	@Nullable
	public NuGetConfigFile getNuGetConfigFile()
	{
        return getXmlFileBySpecifiedPathAndFallbackName(myNuGetConfigFileUrl, NUGET_CONFIG, NuGetConfigFile.class);
	}

    public String getPackagesConfigFileUrl()
    {
        return myPackagesConfigFileUrl;
    }

    @Nullable
    public VirtualFile getPackagesConfigVirtualFile()
    {
        return getModuleVirtualFileBySpecifiedPathAndFallbackName(myPackagesConfigFileUrl, PACKAGES_CONFIG);
    }

	@Nullable
	public NuGetPackagesFile getPackagesConfigFile()
	{
        return getXmlFileBySpecifiedPathAndFallbackName(myPackagesConfigFileUrl, PACKAGES_CONFIG, NuGetPackagesFile.class);
	}

    @Nullable
    private VirtualFile getModuleVirtualFileBySpecifiedPathAndFallbackName(@Nullable String specifiedPath, String fallbackFileName)
    {
        VirtualFile file = null;
        if (!StringUtil.isEmpty(specifiedPath))
        {
            file = VirtualFileManager.getInstance().findFileByUrl(specifiedPath);
        }
        if (file == null)
        {
            VirtualFile currentDirectory = getModule().getModuleDir();
            for (int i = 0; i < NUGET_CONFIG_SEARCH_DEPTH && file == null; i++)
            {
                file = currentDirectory.findFileByRelativePath(fallbackFileName);
                currentDirectory = currentDirectory.getParent();
            }
        }
        return file;
    }

	@Nullable
	private <T extends DomElement> T getXmlFileBySpecifiedPathAndFallbackName(@Nullable String specifiedPath, String fallbackFileName, Class<T> tClass)
    {
        VirtualFile file = getModuleVirtualFileBySpecifiedPathAndFallbackName(specifiedPath, fallbackFileName);
        if (file == null)
        {
            return null;
        }

        PsiFile maybeXmlFile = PsiManager.getInstance(getProject()).findFile(file);
        if(!(maybeXmlFile instanceof XmlFile))
        {
            return null;
        }
        DomFileElement<T> fileElement = DomManager.getDomManager(getProject()).getFileElement((XmlFile) maybeXmlFile, tClass);
        return fileElement == null ? null : fileElement.getRootElement();
	}
}
