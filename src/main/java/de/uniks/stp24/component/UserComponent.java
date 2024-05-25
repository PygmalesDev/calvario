package de.uniks.stp24.component;

import de.uniks.stp24.model.MemberUser;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "User.fxml")
public class UserComponent extends StackPane implements ReusableItemComponent<MemberUser> {
    @FXML
    ImageView avatarImageView;
    @FXML
    Text usernameText;
    @FXML
    Text readyText;
    @FXML
    Button kickButton;
    @FXML
    HBox userHBox;
    @Inject
    LobbyService lobbyService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    @Resource
    ResourceBundle resource;

    private final ImageCache imageCache;
    private MemberUser member;

    @Inject
    public UserComponent(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void kickUser() {
        this.subscriber.subscribe(this.lobbyService.leaveLobby(
                this.member.game()._id(), this.member.user()._id()
        ));
    }

    @Override
    public void setItem(@NotNull MemberUser member) {
        this.member = member;
        this.kickButton.setId("kick"+member.user()._id());
        if (member.user()._id().equals(member.game().owner()) || !member.asHost())
            this.userHBox.getChildren().remove(this.kickButton);

        this.usernameText.setText(member.user().name());
        if (member.ready())
            this.readyText.setText("Ready");
        else
            this.readyText.setText("Not Ready");

        this.avatarImageView.setImage(this.imageCache.get(Objects.nonNull(this.member.user().avatar())
                ? this.member.user().avatar()
                : "test/911.png" ));
    }
}
