/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.openflexo.foundation.fml.FMLModelContext;
import org.openflexo.foundation.fml.FMLModelContext.FMLEntity;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.TechnologyAdapterController;

/**
 * Abstract document generator
 * 
 */
public abstract class AbstractGenerator<O extends FMLObject> {

	private static final Logger logger = FlexoLogger.getLogger(AbstractGenerator.class.getPackage().getName());

	private Class<O> objectClass;
	private TADocGenerator<?> taDocGenerator;
	private File generatedFile;
	protected File smallIconFile;
	protected File bigIconFile;

	public AbstractGenerator(Class<O> objectClass, TADocGenerator<?> taDocGenerator) {
		this.objectClass = objectClass;
		this.taDocGenerator = taDocGenerator;
		generatedFile = new File(taDocGenerator.getMDDir(), objectClass.getSimpleName() + ".md");
		System.out.println("Will generate: " + generatedFile.getAbsolutePath());
	}

	public abstract void generate();

	protected void render(StringBuffer sb) {
		try {
			FileUtils.saveToFile(generatedFile, sb.toString());
			System.out.println("Generated " + generatedFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String getGlobalReference() {
		return taDocGenerator.getRelativePath() + "/" + objectClass.getSimpleName() + ".html";
	}

	protected String getGlobalRolesReference() {
		return taDocGenerator.getRelativePath() + "/" + objectClass.getSimpleName() + "_roles.html";
	}

	protected String getGlobalBehavioursReference() {
		return taDocGenerator.getRelativePath() + "/" + objectClass.getSimpleName() + "_behaviours.html";
	}

	protected String getGlobalEditionActionsReference() {
		return taDocGenerator.getRelativePath() + "/" + objectClass.getSimpleName() + "_edition_actions.html";
	}

	protected String getGlobalFetchRequestsReference() {
		return taDocGenerator.getRelativePath() + "/" + objectClass.getSimpleName() + "_fetch_requests.html";
	}

	protected String getLocalReference() {
		return "./" + objectClass.getSimpleName() + ".html";
	}

	protected String getLocalRolesReference() {
		return "./" + objectClass.getSimpleName() + "_roles.html";
	}

	protected String getLocalBehavioursReference() {
		return "./" + objectClass.getSimpleName() + "_behaviours.html";
	}

	protected String getLocalEditionActionsReference() {
		return "./" + objectClass.getSimpleName() + "_edition_actions.html";
	}

	protected String getLocalFetchRequestsReference() {
		return "./" + objectClass.getSimpleName() + "_fetch_requests.html";
	}

	protected abstract Image getIcon();

	protected String getSmallIconAsHTML() {
		if (smallIconFile == null) {
			return "";
		}
		return "<img src=\"" + "./images/" + smallIconFile.getName() + "\" alt=\"" + getObjectClass().getSimpleName() + "\"/>";
	}

	protected String getBigIconAsHTML() {
		if (bigIconFile == null) {
			return "";
		}
		return "<img src=\"" + "./images/" + bigIconFile.getName() + "\" alt=\"" + getObjectClass().getSimpleName() + "\"/>";
	}

	protected void generateIconFiles() {
		if (getIcon() != null) {
			smallIconFile = new File(taDocGenerator.getImageDir(), objectClass.getSimpleName() + ".png");
			saveImage(getIcon(), smallIconFile);
			System.out.println("Generated " + smallIconFile.getAbsolutePath());
			bigIconFile = new File(taDocGenerator.getImageDir(), objectClass.getSimpleName() + "_BIG.png");
			saveImage(getIcon(), bigIconFile, 2.0);
			System.out.println("Generated " + bigIconFile.getAbsolutePath());
		}
		else {
			System.out.println("Cannot find icon for " + objectClass.getSimpleName());
		}
	}

	protected static void saveImage(Image image, File file) {
		saveImage(image, file, null);
	}

	protected static void saveImage(Image image, File file, Double scaleFactor) {
		try {
			ImageIO.write(imageToBufferedImage(image, scaleFactor), "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static BufferedImage imageToBufferedImage(Image im, Double scaleFactor) {
		final int w = im.getWidth(null);
		final int h = im.getHeight(null);
		BufferedImage imageToSave;
		if (scaleFactor != null) {
			BufferedImage scaledImage = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
			final AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
			final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
			BufferedImage initialImage = imageToBufferedImage(im, null);
			scaledImage = ato.filter(initialImage, scaledImage);
			imageToSave = scaledImage;
		}
		else {
			imageToSave = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics bg = imageToSave.getGraphics();
			bg.drawImage(im, 0, 0, null);
			bg.dispose();
		}
		return imageToSave;
	}

	public Class<O> getObjectClass() {
		return objectClass;
	}

	public String toMD(String text) {
		String returned = text;
		if (returned.startsWith("<html>")) {
			returned = returned.substring(6);
		}
		if (returned.endsWith("</html>")) {
			returned = returned.substring(0, returned.length() - 7);
		}
		return returned;
	}

	protected FMLEntity<?> getFMLEntity() {
		return FMLModelContext.getFMLEntity(getObjectClass(), getFMLModelFactory());
	}

	public final String getFMLKeyword() {
		if (getFMLEntity() != null) {
			return getFMLEntity().getFmlAnnotation().value();
		}
		return getObjectClass().getSimpleName();
	}

	public final String getFMLDescription() {
		if (getFMLEntity() != null) {
			String returned = getFMLEntity().getFmlAnnotation().description();
			if (StringUtils.isEmpty(returned)) {
				return "No documentation yet";
			}
			return toMD(getFMLEntity().getFmlAnnotation().description());
		}
		return "No documentation yet";
	}

	/*@Override
	public final boolean hasFMLProperties(FMLModelFactory modelFactory) {
		if (getFMLEntity(modelFactory) != null) {
			return getFMLEntity(modelFactory).getProperties().size() > 0;
		}
		return false;
	}
	
	@Override
	public Set<FMLProperty> getFMLProperties(FMLModelFactory modelFactory) {
		if (getFMLEntity(modelFactory) != null) {
			return (Set) getFMLEntity(modelFactory).getProperties();
		}
		return null;
	}
	
	@Override
	public FMLProperty getFMLProperty(String propertyName, FMLModelFactory modelFactory) {
		Set<FMLProperty> fmlProperties = getFMLProperties(modelFactory);
		if (fmlProperties != null) {
			for (FMLProperty fmlProperty : getFMLProperties(modelFactory)) {
				if (fmlProperty.getName().equals(propertyName)) {
					return fmlProperty;
				}
			}
		}
		return null;
	}*/

	public TechnologyAdapterController<?> getTechnologyAdapterController() {
		return taDocGenerator.getTechnologyAdapterController();
	}

	public FMLModelFactory getFMLModelFactory() {
		return taDocGenerator.getFMLModelFactory();
	}

}
