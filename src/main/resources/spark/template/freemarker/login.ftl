<#import "./base.ftl" as base>

<@base.base title="Log In" context="login">

    <div class="container container--white mt-3">

        <div class="row">
            <div class="col-12 p-3">
                <h2>Log In</h2>

                <form action="/admin/login" method="POST">

                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" class="form-control" name="username" id="username" />
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" class="form-control" name="password" id="password" />
                    </div>
                    <button type="submit" class="btn btn-primary">Log In</button>
                </form>
            </div>
        </div>

    </div>

</@base.base>