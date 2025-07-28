package com.projectkorra.rpg.modules.randomavatar.avatar;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.modules.randomavatar.util.EndReason;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The fields could be redundant since an AvatarEntity could actually just be a {@link BendingPlayer} with Start and end times and reason
 * But as spoke about in the PR, this structure is very WIP and I would implement something like an AvatarRepository interface or similar, maybe.
 */
public class AvatarEntity {
    private final UUID uuid;
    private final Element mainElement;
    private final List<Element.SubElement> subElements;

    private Instant chosenTime;
    private Instant endTime;
    private EndReason endReason;

    public AvatarEntity(UUID uuid, Element mainElement, List<Element.SubElement> subElements) {
        this.uuid = uuid;
        this.mainElement = mainElement;
        this.subElements = new ArrayList<>(); // Init empty List in case of no sub elements

        if (!subElements.isEmpty()) {
            this.subElements.addAll(subElements);
        }
    }

    public AvatarEntity(UUID uuid, Element mainElement, List<Element.SubElement> subElements, Instant chosenTime, Instant endTime, EndReason reason) {
        this(uuid, mainElement, subElements);
        this.chosenTime = chosenTime;
        this.endTime = endTime;
        this.endReason = reason;
    }

    public void handleInitiation() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            throw new NullPointerException("Couldn't initiate new Avatar! Player is null!");
        }

        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer == null) {
            throw new NullPointerException("Couldn't initiate new Avatar! BendingPlayer is null!");
        }

        Element element = bendingPlayer.getElements().getFirst();
        List<Element.SubElement> subElement = bendingPlayer.getSubElements();
        this.chosenTime = Instant.now();

        if (element == null || subElement == null || this.chosenTime == null) {
            throw new NullPointerException("Couldn't initiate new Avatar! Element, SubElement or ChosenTime is null!");
        }

        AvatarManager.storeAvatar(player.getUniqueId(), element, subElement, this.chosenTime);

        removeAllElements(bendingPlayer);
        addAvatarElements(bendingPlayer);

        ChatUtil.sendBrandingMessage(player, Element.AVATAR.getColor() + "You have been chose as Avatar! Restore the balance in this world and bring peace!");
    }

    private void removeAllElements(BendingPlayer bendingPlayer) {
        if (!bendingPlayer.getElements().isEmpty()) {
            bendingPlayer.getElements().clear();

            if (!bendingPlayer.getSubElements().isEmpty()) {
                bendingPlayer.getSubElements().clear();
            }
        }

        if (bendingPlayer.hasTempElements()) {
            bendingPlayer.getTempElements().clear();

            if (!bendingPlayer.getTempSubElements().isEmpty()) {
                bendingPlayer.getTempSubElements().clear();
            }
        }
    }

    private void addAvatarElements(BendingPlayer bendingPlayer) {
        for (Element mainElements : new Element[]{Element.FIRE, Element.WATER, Element.EARTH, Element.AIR}) {
            bendingPlayer.addElement(mainElements);
            bendingPlayer.saveElements();
        }
    }

    public void handleDeath(boolean inAvatarState) {

    }

    public AvatarEntity getPreviousAvatar(UUID previousAvatarUUID) {
        return AvatarManager.getPreviousAvatars().get(previousAvatarUUID) != null ? AvatarManager.getPreviousAvatars().get(previousAvatarUUID) : null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Element getMainElement() {
        return mainElement;
    }

    public List<Element.SubElement> getSubElements() {
        return subElements;
    }

    public Instant getChosenTime() {
        return chosenTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public EndReason getEndReason() {
        return endReason;
    }

    public void setChosenTime(Instant chosenTime) {
        this.chosenTime = chosenTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public void setEndReason(EndReason endReason) {
        this.endReason = endReason;
    }
}
