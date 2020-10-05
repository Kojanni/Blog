package com.kochetkova.service.impl.enums;

/**
 *  mode - режим сложности PostResponse:
 *  ShortInfoCount - простой: id, время, пользователь, заголовок, анонс текста, кол-во диз/лайк, комментов, просмотров
 *  InfoCountCommentTag - усложненный: с комментариями и тегами, вместо анонса полный текст, юзер с фото.
 */
public enum ModePostInfo {
    SHORTINFO_COUNT,
    INFO_COUNT_COMMENT_TAG
}
