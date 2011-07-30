// ----------------------------------------------------------------------------
// markItUp!
// ----------------------------------------------------------------------------
// Copyright (C) 2008 Jay Salvat
// © Oleksandr Natalenko, 2011
// http://markitup.jaysalvat.com/
// ----------------------------------------------------------------------------
myLORSettings = {
  nameSpace:          "lor", // Useful to prevent multi-instances CSS conflict
  markupSet: [
      {name:'Полужирный', key:'B', openWith:'[b]', closeWith:'[/b]'}, 
      {name:'Курсив', key:'I', openWith:'[i]', closeWith:'[/i]'}, 
      {name:'Подчёркнутый', key:'U', openWith:'[u]', closeWith:'[/u]'}, 
      {name:'Перечёркнутый', key:'S', openWith:'[s]', closeWith:'[/s]'},
      {separator:'---------------' },
      {name:'Ссылка', key:'L', openWith:'[url=[![URL-адрес ссылки:]!]]', closeWith:'[/url]', placeHolder:'Текст ссылки'},
      {separator:'---------------' },
      {name:'Цитата', key:'Q', openWith:'[quote=[![Источник:]!]]', closeWith:'[/quote]', placeHolder:'Текст цитаты'},
      {name:'Код', key:'C', openWith:'[code=[![Язык программирования: bash, shell, c, cpp, css, delphi, diff, java, js, javascript, html, lisp, pascal, patch, perl, php, plain, python, ruby, scheme, xml]!]]', closeWith:'[/code]', placeHolder:'Код'},
      {separator:'---------------' },
      {name:'Пользователь', key:'E', openWith:'[user]', closeWith:'[/user]'},
      {separator:'---------------' },
      {name:'Ненумерованный список', openWith:'[list]\n', closeWith:'\n[/list]'}, 
      {name:'Нумерованный список', openWith:'[list=[![Тип списка: 1, a, A, i. I]!]]\n', closeWith:'\n[/list]'}, 
      {name:'Элемент списка', openWith:'[*] '}, 
      {separator:'---------------' },
      {name:'Очистить', className:"clean", replaceWith:function(h) { return h.selection.replace(/\[(.*?)\]/g, "") } },
   ]
}
