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

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * @author VISTALL
 * @since 24.11.14
 */
public class NuGetMutableModuleExtension extends NuGetModuleExtension implements MutableModuleExtension<NuGetModuleExtension>
{
	public NuGetMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

    public void setNuGetConfigFilePath(String path)
    {
        if(StringUtil.isEmptyOrSpaces(path))
        {
            myNuGetConfigFileUrl = null;
        }
        else
        {
            myNuGetConfigFileUrl = VirtualFileManager.constructUrl(StandardFileSystems.FILE_PROTOCOL, path);
        }
    }

    public void setPackagesConfigFilePath(String path)
    {
        if(StringUtil.isEmptyOrSpaces(path))
        {
            myPackagesConfigFileUrl = null;
        }
        else
        {
            myPackagesConfigFileUrl = VirtualFileManager.constructUrl(StandardFileSystems.FILE_PROTOCOL, path);
        }
    }

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull Runnable runnable)
	{
		return new NuGetConfigPanel(this);
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@Override
	public boolean isModified(@NotNull NuGetModuleExtension nuGetModuleExtension)
	{
		return myIsEnabled != nuGetModuleExtension.isEnabled() ||
                !Comparing.equal(myNuGetConfigFileUrl, nuGetModuleExtension.myNuGetConfigFileUrl) ||
                !Comparing.equal(myPackagesConfigFileUrl, nuGetModuleExtension.myPackagesConfigFileUrl);
	}
}
