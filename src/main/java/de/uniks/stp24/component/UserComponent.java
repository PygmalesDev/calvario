package de.uniks.stp24.component;

import de.uniks.stp24.model.MemberUser;
import de.uniks.stp24.model.User;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Objects;

@Component(view = "User.fxml")
public class UserComponent extends HBox implements ReusableItemComponent<MemberUser> {
    @FXML
    ImageView avatarImageView;
    @FXML
    Text usernameText;
    @FXML
    Text readyText;

    private final ImageCache imageCache;

    @Inject
    public UserComponent(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    @Override
    public void setItem(@NotNull MemberUser member) {
        this.usernameText.setText(member.user().name());
        if (member.ready())
            this.readyText.setText("Ready");
        else
            this.readyText.setText("Not Ready");
        if (Objects.nonNull(member.user().avatar()))
            this.avatarImageView.setImage(this.imageCache.get(member.user().avatar()));
        else
            this.avatarImageView.setImage(this.imageCache.get("Icons/Eye_Icon_32.png"));
    }
}
