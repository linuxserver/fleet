<#import "../ui/form/input.ftl"         as input />
<#import "../ui/elements/tag.ftl"       as tag />
<#import "../ui/components/message.ftl" as message />
<#import "../prebuilt/base.ftl"         as base />

<@base.base title="Images" context="home" showTitle=false>

    <section class="section">
        <div class="container">

            <div class="columns is-multiline is-centered">
                <div class="column is-8-desktop">
                    <@input.text id="SearchImages" icon="search" size="normal" placeholder="Search..." />
                </div>
            </div>

        </div>
    </section>

    <section class="section">
        <div class="container">

            <article class="media">
                <figure class="media-left">
                    <p class="image is-64x64"></p>
                </figure>
                <div class="media-content">
                    <div class="content">
                        <span class="image-title">linuxserver / <span class="has-text-weight-bold"><a class="has-text-grey-dark" href="">sonarr</a></span></span>
                    </div>
                </div>
            </article>

        </div>
    </section>

</@base.base>