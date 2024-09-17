package mod.kerzox.brewchemy.client.ui.screen.base;


import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;

public interface ICustomScreen {
    int getGuiLeft();
    int getGuiTop() ;
    DefaultMenu<?> getMenu();
}
