package de.uniks.stp24.component;

import de.uniks.stp24.model.User;
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
public class UserComponent extends HBox implements ReusableItemComponent<User> {
    @FXML
    ImageView avatarImageView;
    @FXML
    Text usernameText;

    @Inject
    public UserComponent() {

    }

    @Override
    public void setItem(@NotNull User user) {
        this.usernameText.setText(user.name());
        if (Objects.nonNull(user.avatar()))
            this.avatarImageView.setImage(new Image(user.avatar()));
    }
}
