+++++++++++Примечания:

логин должен вводится в формате email: символ(ы)@символ(ы).символы

пароль не должен содержать никаких символов, кроме букв и цифр

удаление недвижимости в списке происходит через контекстное меню

+++++++++++Сильные стороны приложения:

Использование корутинов при проверке правильности логина и пароля при сохранении нового пользователя

Проверка логина и пароля происходит с использованием регулярных выражений при сохранении нового пользователя

Использование БД без sql - Object Box

+++++++++++Слабые стороны приложения:

Нет подсказок для удаления

Нет подсказок для введения корректного пароля и логина

Нет использования фрагментов

Не совершенная проверка логина на корректность:

	-нет проверки на корректность почтового ящика

	-нет подтверждения почты

При проверке логина и пароля не учитывается регистр