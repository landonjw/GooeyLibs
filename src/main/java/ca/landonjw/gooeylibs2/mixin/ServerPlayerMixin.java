package ca.landonjw.gooeylibs2.mixin;

import ca.landonjw.gooeylibs2.implementation.GooeyContainer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin extends PlayerMixin {

    @Inject(method = "doCloseContainer", at = @At(value = "INVOKE", target = "net/minecraft/world/inventory/InventoryMenu.transferState (Lnet/minecraft/world/inventory/AbstractContainerMenu;)V"), cancellable = true)
    public void transferState(CallbackInfo ci) {
        if(this.containerMenu instanceof GooeyContainer) {
            System.out.println("This one isn't for you.");
            ci.cancel();
        }
    }
}
