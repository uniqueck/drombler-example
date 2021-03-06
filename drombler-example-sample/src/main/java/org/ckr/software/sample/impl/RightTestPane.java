
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ckr.software.sample.impl;

import org.ckr.software.sample.ColoredCircle;
import org.ckr.software.sample.ColoredRectangle;
import org.ckr.software.sample.Sample;
import java.util.EnumMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.drombler.acp.core.docking.ViewDocking;
import org.drombler.acp.core.docking.WindowMenuEntry;
import org.drombler.commons.client.util.ResourceBundleUtils;
import org.drombler.commons.context.ActiveContextSensitive;
import org.drombler.commons.context.Context;
import org.drombler.commons.context.ContextEvent;
import org.drombler.commons.context.LocalContextProvider;
import org.drombler.commons.context.SimpleContext;
import org.drombler.commons.context.SimpleContextContent;
import org.drombler.commons.fx.fxml.FXMLLoaders;

@ViewDocking(areaId = "right", position = 10, displayName = "%RightTestPane.displayName", icon = "right-test-pane.png",
        accelerator = "Shortcut+4", resourceBundleBaseName = ResourceBundleUtils.PACKAGE_RESOURCE_BUNDLE_BASE_NAME,
        menuEntry = @WindowMenuEntry(path = "Other", position = 40))
public class RightTestPane extends GridPane implements ActiveContextSensitive, LocalContextProvider {

    private final SimpleContextContent contextContent = new SimpleContextContent();
    private final SimpleContext context = new SimpleContext(contextContent);
    private Context activeContext;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView coloredCircleImageView;
    @FXML
    private ImageView redRectangleImageView;
    @FXML
    private ImageView yellowRectangleImageView;
    @FXML
    private ImageView blueRectangleImageView;
    private final Map<ColoredRectangle, ImageView> coloredRectangleImageViews = new EnumMap<>(ColoredRectangle.class);
    private final ColoredCircleChangeListener coloredCircleChangeListener = new ColoredCircleChangeListener();
    private final ColoredRectanglesSetChangeListener coloredRectanglesSetChangeListener = new ColoredRectanglesSetChangeListener();
    private SampleHandler sampleHandler;

    public RightTestPane() {
        loadFXML();
        initColoredRectangleImageViewsMap();
    }

    private void loadFXML() {
        FXMLLoaders.loadRoot(this, ResourceBundleUtils.getPackageResourceBundle(RightTestPane.class));
    }

    @Override
    public Context getLocalContext() {
        return context;
    }

    private void initColoredRectangleImageViewsMap() {
        coloredRectangleImageViews.put(ColoredRectangle.RED, redRectangleImageView);
        coloredRectangleImageViews.put(ColoredRectangle.YELLOW, yellowRectangleImageView);
        coloredRectangleImageViews.put(ColoredRectangle.BLUE, blueRectangleImageView);
    }

    @Override
    public void setActiveContext(Context activeContext) {
        this.activeContext = activeContext;
        this.activeContext.addContextListener(SampleHandler.class, (ContextEvent event) -> contextChanged());
        contextChanged();
    }

    private void contextChanged() {
        SampleHandler newSampleHandler = activeContext.find(SampleHandler.class);
        if ((sampleHandler == null && newSampleHandler != null) || (sampleHandler != null && !sampleHandler.equals(newSampleHandler))) {
            if (sampleHandler != null) {
                unregister();
            }
            sampleHandler = newSampleHandler;
            if (sampleHandler != null) {
                register();
            }
        }
    }

    private void unregister() {
        contextContent.remove(sampleHandler);

        nameLabel.textProperty().unbind();
        nameLabel.setText(null);

        Sample sample = sampleHandler.getSample();
        sample.coloredCircleProperty().removeListener(coloredCircleChangeListener);
        coloredCircleImageView.setImage(null);

        sample.getColoredRectangles().removeListener(coloredRectanglesSetChangeListener);
        resetColoredRectangleImageViews();
    }

    private void register() {
        Sample sample = sampleHandler.getSample();
        nameLabel.textProperty().bind(sample.nameProperty());

        sample.coloredCircleProperty().addListener(coloredCircleChangeListener);
        configureColoredCircleImageView(sample.getColoredCircle());

        initColoredRectangleImageViews();
        sample.getColoredRectangles().addListener(coloredRectanglesSetChangeListener);

        contextContent.add(sampleHandler);
    }

    private void configureColoredCircleImageView(ColoredCircle coloredCircle) {
        coloredCircleImageView.setImage(coloredCircle.getImage());
    }

    private void setColoredRectangleImage(ColoredRectangle coloredRectangle) {
        coloredRectangleImageViews.get(coloredRectangle).setImage(coloredRectangle.getImage());
    }

    private void initColoredRectangleImageViews() {
        for (ColoredRectangle coloredRectangle : ColoredRectangle.values()) {
            if (sampleHandler.getSample().getColoredRectangles().contains(coloredRectangle)) {
                setColoredRectangleImage(coloredRectangle);
            }
        }
    }

    private void resetColoredRectangleImageViews() {
        for (ColoredRectangle coloredRectangle : ColoredRectangle.values()) {
            coloredRectangleImageViews.get(coloredRectangle).setImage(null);
        }
    }

    private class ColoredCircleChangeListener implements ChangeListener<ColoredCircle> {

        @Override
        public void changed(ObservableValue<? extends ColoredCircle> ov, ColoredCircle oldeValue, ColoredCircle newValue) {
            configureColoredCircleImageView(newValue);
        }
    }

    private class ColoredRectanglesSetChangeListener implements SetChangeListener<ColoredRectangle> {

        @Override
        public void onChanged(SetChangeListener.Change<? extends ColoredRectangle> change) {
            if (change.wasAdded()) {
                setColoredRectangleImage(change.getElementAdded());
            } else if (change.wasRemoved()) {
                coloredRectangleImageViews.get(change.getElementRemoved()).setImage(null);
            }
        }
    }
}

